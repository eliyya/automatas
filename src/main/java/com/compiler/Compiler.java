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
        if (!CompilerConstants.ARITHMETIC.contains(op.value())) {
            throw new RuntimeException("Expected an arithmetic operator, found " + op.value());
        }
        checkExpression(node.lhs());
        checkExpression(node.rhs());
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

    // public void check() {
    //     // Hasmap<nombre, tipo>
    //     // var types = new HashMap<String, String>();
    //     for (var line : this.lines) {
    //         switch (line) {
    //             case Atom atom -> {
    //                 if (atom.getToken().type() != TokenType.EOF) {
    //                     throw new RuntimeException("Unexpected token: " + atom.getToken().value());
    //                 }
    //             }
    //             case Instruction instruction -> {
    //                 checkInstruction(instruction);
    //             }
    //             default ->
    //                 throw new AssertionError();
    //         }
    //     }
    // }
    // private void checkInstruction(Instruction instruction) {
    //     var lhs = instruction.getLhs();
    //     var rhs = instruction.getRhs();
    //     if (lhs instanceof Declaration declaration) {
    //         checkDeclaration(declaration);
    //     }
    //     System.out.println("lhs: " + lhs);
    //     System.out.println("rhs: " + rhs);
    //     // if (rhs instanceof Expression expression) {
    //     //     checkExpression(expression);
    //     // }
    // }
    // private void checkDeclaration(Declaration declaration) {
    //     var type = declaration.getType();
    //     var rhs = declaration.getExpression();
    //     switch (rhs) {
    //         case Atom atom -> {
    //             if (types.containsKey(atom.getToken().value())) {
    //                 throw new RuntimeException("Variable " + atom.getToken().value() + " already declared");
    //             } else {
    //                 types.put(atom.getToken().value(), type.value());
    //             }
    //         }
    //         case Expression exp -> {
    //             var op = exp.getOp();
    //             if (op.value().equals("=")) {
    //                 checkDeclarationAssignment(exp, type);
    //             } else if (op.value().equals(",")) {
    //                 var elhs = exp.getLhs();
    //                 var erhs = exp.getRhs();
    //                 checkDeclarationAssignment(elhs, type);
    //                 checkDeclarationAssignment(erhs, type);
    //             }
    //         }
    //         default -> {
    //         }
    //     }
    //     System.out.println("type: " + type.value());
    //     System.out.println("expression: " + rhs);
    // }
    // private void checkDeclarationAssignment(Node e, Token type) {
    //     if (e instanceof Expression expression) {
    //         if (expression.getOp().value().equals(",")) {
    //             var elhs = expression.getLhs();
    //             var erhs = expression.getRhs();
    //             checkDeclarationAssignment(elhs, type);
    //             checkDeclarationAssignment(erhs, type);
    //             return;
    //         }
    //         var op = expression.getOp();
    //         if (!op.value().equals("=")) {
    //             throw new RuntimeException("1: Expected =, found " + op.value());
    //         }
    //         var lhs = expression.getLhs();
    //         if (lhs instanceof Atom atom) {
    //             if (atom.getToken().type() != TokenType.IDENTIFICATOR) {
    //                 throw new SyntaxError(TokenType.IDENTIFICATOR.name(), atom.getToken().value(), atom.getToken().line());
    //             }
    //             if (types.containsKey(atom.getToken().value())) {
    //                 throw new RuntimeException("Variable " + atom.getToken().value() + " already declared");
    //             }
    //             types.put(atom.getToken().value(), type.value());
    //         } else {
    //             throw new SyntaxError(TokenType.IDENTIFICATOR.name(), lhs.toString(), op.line());
    //         }
    //         var rhs = expression.getRhs();
    //         switch (rhs) {
    //             case Atom rAtom -> {
    //                 if (!(rAtom.getToken().type() == TokenType.IDENTIFICATOR || rAtom.getToken().type() == TokenType.LITERAL)) {
    //                     throw new SyntaxError(TokenType.IDENTIFICATOR.name(), rAtom.getToken().value(), rAtom.getToken().line());
    //                 }
    //                 if (null == rAtom.getToken().type()) {
    //                     throw new RuntimeException("Expected variable or literal, found " + rAtom.getToken().value());
    //                 } else {
    //                     switch (rAtom.getToken().type()) {
    //                         case IDENTIFICATOR -> {
    //                             if (!types.containsKey(rAtom.getToken().value())) {
    //                                 throw new RuntimeException("Variable " + rAtom.getToken().value() + " not declared");
    //                             }
    //                             if (!types.get(rAtom.getToken().value()).equals(type.value())) {
    //                                 throw new RuntimeException("Variable " + rAtom.getToken().value() + " is not of type " + type.value());
    //                             }
    //                         }
    //                         case LITERAL -> {
    //                             var literalType = checkType(rAtom.getToken());
    //                             if (!literalType.equals(type.value())) {
    //                                 throw new RuntimeException("Literal " + rAtom.getToken().value() + " is not of type " + type.value());
    //                             }
    //                         }
    //                         default ->
    //                             throw new RuntimeException("Expected variable or literal, found " + rAtom.getToken().value());
    //                     }
    //                 }
    //                 types.put(rAtom.getToken().value(), type.value());
    //             }
    //             case Expression rExp ->
    //                 checkDeclarationAssignment(rExp, type);
    //             default -> {
    //             }
    //         }
    //     } else {
    //         throw new RuntimeException("Expected expression, found " + e.toString());
    //     }
    // }
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
