package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.compiler.nodes.Atom;
import com.compiler.nodes.Declaration;
import com.compiler.nodes.Expression;
import com.compiler.nodes.Instruction;
import com.compiler.nodes.Node;
import com.compiler.utils.SyntaxError;
import com.compiler.utils.TokenType;

public class Compiler {

    private final Analizer analizer;
    private ArrayList<Node> lines;
    // HashMap<nombre, tipo>
    private HashMap<String, String> types = new HashMap<>();

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

    public void check() {
        // Hasmap<nombre, tipo>
        // var types = new HashMap<String, String>();
        for (var line : this.lines) {
            switch (line) {
                case Atom atom -> {
                    if (atom.getToken().type() != TokenType.EOF) {
                        throw new RuntimeException("Unexpected token: " + atom.getToken().value());
                    }
                }
                case Instruction instruction -> {
                    checkInstruction(instruction);
                }
                default ->
                    throw new AssertionError();
            }
        }
    }

    private void checkInstruction(Instruction instruction) {
        var lhs = instruction.getLhs();
        var rhs = instruction.getRhs();
        if (lhs instanceof Declaration declaration) {
            checkDeclaration(declaration);
        }
        System.out.println("lhs: " + lhs);
        System.out.println("rhs: " + rhs);
        // if (rhs instanceof Expression expression) {
        //     checkExpression(expression);
        // }
    }

    private void checkDeclaration(Declaration declaration) {
        var type = declaration.getType();
        var rhs = declaration.getExpression();
        if (rhs instanceof Atom atom) {
            if (types.containsKey(atom.getToken().value())) {
                throw new RuntimeException("Variable " + atom.getToken().value() + " already declared");
            } else {
                types.put(atom.getToken().value(), type.value());
            }
        } else if (rhs instanceof Expression exp) {
            var op = exp.getOp();
            if (op.value().equals("=")) {
                checkDeclarationAssignment(exp, type);
            }
        }
        System.out.println("type: " + type.value());
        System.out.println("expression: " + rhs);
    }

    private void checkDeclarationAssignment(Expression expression, Token type) {
        var op = expression.getOp();
        if (!op.value().equals("=")) {
            throw new RuntimeException("Expected =, found " + op.value());
        }
        var lhs = expression.getLhs();
        if (lhs instanceof Atom atom) {
            if (atom.getToken().type() != TokenType.IDENTIFICATOR) {
                throw new SyntaxError(TokenType.IDENTIFICATOR.name(), atom.getToken().value(), atom.getToken().line());
            }
            if (types.containsKey(atom.getToken().value())) {
                throw new RuntimeException("Variable " + atom.getToken().value() + " already declared");
            }
            types.put(atom.getToken().value(), type.value());
        } else {
            throw new SyntaxError(TokenType.IDENTIFICATOR.name(), lhs.toString(), op.line());
        }
        var rhs = expression.getRhs();
        if (rhs instanceof Atom rAtom) {
            if (!(rAtom.getToken().type() == TokenType.IDENTIFICATOR || rAtom.getToken().type() == TokenType.LITERAL)) {
                throw new SyntaxError(TokenType.IDENTIFICATOR.name(), rAtom.getToken().value(), rAtom.getToken().line());
            }
            if (rAtom.getToken().type() == TokenType.IDENTIFICATOR) {
                if (!types.containsKey(rAtom.getToken().value())) {
                    throw new RuntimeException("Variable " + rAtom.getToken().value() + " not declared");
                }
                if (!types.get(rAtom.getToken().value()).equals(type.value())) {
                    throw new RuntimeException("Variable " + rAtom.getToken().value() + " is not of type " + type.value());
                }
            } else if (rAtom.getToken().type() == TokenType.LITERAL) {
                var literalType = checkType(rAtom.getToken());
                if (!literalType.equals(type.value())) {
                    throw new RuntimeException("Literal " + rAtom.getToken().value() + " is not of type " + type.value());
                }
            } else {
                throw new RuntimeException("Expected variable or literal, found " + rAtom.getToken().value());
            }
            types.put(rAtom.getToken().value(), type.value());
        } else if (rhs instanceof Expression rExp) {
            checkDeclarationAssignment(rExp, type);
        }
        System.out.println("lhs: " + lhs);
        System.out.println("rhs: " + rhs);
    }

    private String checkType(Token token) {
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
    // private void checkExpression(Expression expression) {
    //     var op = expression.getOp();
    //     var lhs = expression.getLhs();
    //     var rhs = expression.getRhs();
    // }

    // private void checkLine(Node line) {
    // }
    // private void checkInstruction(Instruction instruction) {
    // }
    // private void checkDeclaration(Declaration declaration) {
    // }
    // private void checkExpression(Expression expression) {
    // }
}
