package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.compiler.utils.CompilerConstants;
import com.compiler.utils.TokenType;

public class Compiler {

    private final Analizer analizer;
    private ArrayList<Node> lines;
    // HashMap<nombre, tipo>
    private final HashMap<String, String> types = new HashMap<>();

    Compiler(File file) throws FileNotFoundException {
        this.analizer = new Analizer(file);
    }

    public void compile() throws FileNotFoundException {
        // ! tokenize file
        this.analizer.tokenize();

        this.analizer.writeTokensFile();

        // ! parse instructions
        this.lines = this.analizer.analize();

        this.analizer.writeAsignationFile();
        this.analizer.writeAsignationTreeFile();

        this.check();
    }

    void check() {
        for (var i = 0; i < this.lines.size(); i++) {
            this.check(this.lines.get(i), false);
        }
        System.out.println("No errors found");
    }

    void check(Node node, boolean isDeclaration) {
        var op = node.op();
        if (op.value().equals(";")) {
            this.checkInstruction(node);
        } else if (op.value().equals(CompilerConstants.DECLARATION)) {
            this.checkDeclaration(node);
        } else if (!op.value().equals(CompilerConstants.EOF)) {
            checkExpression(node);
        }
    }

    void checkExpression(Node node) {
        if (node.isAtom()) {
            this.checkAtom(node);
        } else {
            this.checkBinary(node);
        }
    }

    void checkAtom(Node node) {
        var op = node.op();
        if (op.type().equals(TokenType.IDENTIFICATOR)) {
            if (!this.types.containsKey(op.value())) {
                throw new RuntimeException("Variable " + op.value() + " not declared");
            }
        }
    }

    void checkBinary(Node node) {
        var op = node.op();
        if (!(CompilerConstants.ARITHMETIC.contains(op.value()) || CompilerConstants.ASIGNATIONS.contains(op.value()))) {
            throw new RuntimeException("Expected an arithmetic operator, found " + op.value());
        }
        if (CompilerConstants.ASIGNATIONS.contains(op.value())) {
            this.checkAssignation(node);
        } else {
            checkExpression(node.lhs());
            checkExpression(node.rhs());
        }
    }

    void checkAssignation(Node node) {
        var op = node.op();
        if (!CompilerConstants.ASIGNATIONS.contains(op.value())) {
            throw new RuntimeException("Expected an assignation operator, found " + op.value());
        }
        var type = node.lhs().op().value();
        if (!this.types.containsKey(type)) {
            throw new RuntimeException("Variable " + type + " not declared");
        }
        checkExpressionType(node.rhs(), this.types.get(type));
    }

    void checkExpressionType(Node node, String type) {
        if (node.isAtom()) {
            var op = node.op();
            if (op.type().equals(TokenType.IDENTIFICATOR)) {
                if (!this.types.containsKey(op.value())) {
                    throw new RuntimeException("Variable " + op.value() + " not declared");
                }
                if (!this.types.get(op.value()).equals(type)) {
                    throw new RuntimeException("Variable " + op.value() + " is not of type " + type);
                }
            } else if (op.type().equals(TokenType.LITERAL) || op.value().equals("true") || op.value().equals("false")) {
                if (!this.getType(op).equals(type)) {
                    throw new RuntimeException("Literal " + op.value() + " is not of type " + type);
                }
            }
        } else {
            checkExpressionType(node.lhs(), type);
            checkExpressionType(node.rhs(), type);
        }
    }

    void checkInstruction(Node node) {
        if (!node.op().value().equals(";")) {
            throw new RuntimeException("Expected an instruction, found " + node.op().value());
        }
        check(node.lhs(), false);
        check(node.rhs(), false);
    }

    void checkDeclaration(Node node) {
        if (!node.op().value().equals(CompilerConstants.DECLARATION)) {
            throw new RuntimeException("Expected an instruction, found " + node.op().value());
        }
        var type = node.lhs();
        if (!(type.op().type().equals(TokenType.TYPE) || type.op().type().equals(TokenType.IDENTIFICATOR))) {
            throw new RuntimeException("Expected a type or identifier, found " + type.op().value());
        }
        // check rhs, solo puede ser , o = o identificador 
        var rhs = node.rhs();
        if (!(rhs.op().value().equals(",") || rhs.op().value().equals("=") || rhs.op().type().equals(TokenType.IDENTIFICATOR))) {
            throw new RuntimeException("Expected a identifier, found " + rhs.op().value());
        }
        if (rhs.op().type().equals(TokenType.IDENTIFICATOR)) {
            if (this.types.containsKey(rhs.op().value())) {
                throw new RuntimeException("Variable " + rhs.op().value() + " already declared");
            } else {
                this.types.put(rhs.op().value(), type.op().value());
            }
        } else if (rhs.op().value().equals("=")) {
            this.checkDeclarationAssignment(rhs, type.op().value());
        } else if (rhs.op().value().equals(",")) {
            var elhs = rhs.lhs();
            this.checkDeclarationAssignment(elhs, type.op().value());
            var erhs = rhs.rhs();
            this.checkDeclarationAssignment(erhs, type.op().value());
        }
    }

    void checkDeclarationAssignment(Node node, String type) {
        var op = node.op();
        if (!op.value().equals("=")) {
            throw new RuntimeException("Expected an assignation, found " + op.value());
        }
        var lhs = node.lhs();
        if (!(lhs.op().type().equals(TokenType.IDENTIFICATOR) || lhs.op().type().equals(TokenType.LITERAL))) {
            throw new RuntimeException("Expected a identifier or literal, found " + lhs.op().value());
        }
        var rhs = node.rhs();
        if (rhs.op().type().equals(TokenType.IDENTIFICATOR)) {
            if (!this.types.containsKey(rhs.op().value())) {
                throw new RuntimeException("Variable " + rhs.op().value() + " not declared");
            }
            if (!this.types.get(rhs.op().value()).equals(type)) {
                throw new RuntimeException("Variable " + rhs.op().value() + " is not of type " + type);
            }
        } else if (rhs.op().type().equals(TokenType.LITERAL)) {
            if (!this.getType(rhs.op()).equals(type)) {
                throw new RuntimeException("Literal " + rhs.op().value() + " is not of type " + type);
            }
        } else if (CompilerConstants.ARITHMETIC.contains(rhs.op().value())) {
            var elhs = rhs.lhs();
            checkArithmeticAssignment(elhs, type);
            var erhs = rhs.rhs();
            checkArithmeticAssignment(erhs, type);
        }
    }

    void checkArithmeticAssignment(Node node, String type) {
        if (node.isAtom()) {
            var op = node.op();
            if (op.type().equals(TokenType.IDENTIFICATOR)) {
                if (!this.types.containsKey(op.value())) {
                    throw new RuntimeException("Variable " + op.value() + " not declared");
                }
                if (!this.types.get(op.value()).equals(type)) {
                    throw new RuntimeException("Variable " + op.value() + " is not of type " + type);
                }
            } else if (op.type().equals(TokenType.LITERAL)) {
                if (!this.getType(op).equals(type)) {
                    throw new RuntimeException("Literal " + op.value() + " is not of type " + type);
                }
            }
            return;
        }
        var lhs = node.lhs();
        if (lhs.op().type().equals(TokenType.IDENTIFICATOR)) {
            if (!this.types.containsKey(lhs.op().value())) {
                throw new RuntimeException("Variable " + lhs.op().value() + " not declared");
            }
            if (!this.types.get(lhs.op().value()).equals(type)) {
                throw new RuntimeException("Variable " + lhs.op().value() + " is not of type " + type);
            }
        } else if (lhs.op().type().equals(TokenType.LITERAL)) {
            if (!this.getType(lhs.op()).equals(type)) {
                throw new RuntimeException("Literal " + lhs.op().value() + " is not of type " + type);
            }
        } else if (CompilerConstants.ARITHMETIC.contains(lhs.op().value())) {
            checkArithmeticAssignment(lhs, type);
        }
        var rhs = node.rhs();
        if (rhs.op().type().equals(TokenType.IDENTIFICATOR)) {
            if (!this.types.containsKey(rhs.op().value())) {
                throw new RuntimeException("Variable " + rhs.op().value() + " not declared");
            }
            if (!this.types.get(rhs.op().value()).equals(type)) {
                throw new RuntimeException("Variable " + rhs.op().value() + " is not of type " + type);
            }
        } else if (rhs.op().type().equals(TokenType.LITERAL)) {
            if (!this.getType(rhs.op()).equals(type)) {
                throw new RuntimeException("Literal " + rhs.op().value() + " is not of type " + type);
            }
        } else if (CompilerConstants.ARITHMETIC.contains(rhs.op().value())) {
            checkArithmeticAssignment(rhs, type);
        }
    }

    private String getType(Token token) {
        var value = token.value();
        if (value.matches("^[0-9]+$")) {
            return "int";
        } else if (value.matches("^\\d+\\.\\d+$")) {
            return "float";
        } else if (value.matches("^(true|false)$")) {
            return "boolean";
        } else if (value.matches("^\".*\"$")) {
            return "string";
        } else {
            return "unknown";
        }
    }
}
