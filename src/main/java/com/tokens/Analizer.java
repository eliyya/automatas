package com.tokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Analizer {
    static void addToken(ArrayList<Token> tokens, Token token, boolean semicolon) {
        tokens.add(token);
        if (semicolon) {
            tokens.add(new Token(";", TokenType.SEPARADOR));
        }
    }

    public static ArrayList<Token> tokenize(File file) throws FileNotFoundException {
        var tokens = new ArrayList<Token>();

        // leo un archivo y agrego las lineas al ArrayList
        try (var scanner = new Scanner(file)) {
            var comentarioMultiLinea = false;
            while (scanner.hasNext()) {
                // recorro el ArrayList
                var linea = scanner.nextLine().trim();

                var semicolon = false;

                if (linea.isEmpty()) {
                    tokens.add(new Token("\n", TokenType.EOF));
                    continue;
                }

                // if (linea.startsWith("//")) {
                //     continue;
                // }
                var nuevaLinea = "";
                var lit = false;
                for (String ch : linea.split("")) {
                    if (lit) {
                        nuevaLinea += ch;
                        int countBackslashes = 0;
                        if (!nuevaLinea.endsWith("\"")) {
                            continue;
                        }
                        for (int i = nuevaLinea.length() - 2; i >= 0 && nuevaLinea.charAt(i) == '\\'; i--) {
                            countBackslashes++;
                        }

                        if (countBackslashes % 2 == 0) {
                            nuevaLinea += " ";
                            lit = false;
                        }
                        continue;
                    }
                    if (ch.equals("\"")) {
                        lit = true;
                        nuevaLinea += " \"";
                        continue;
                    }
                    if (ch.equals(";")) {
                        nuevaLinea += " ; ";
                        continue;
                    }
                    if (ch.equals("(")) {
                        nuevaLinea += " ( ";
                        continue;
                    }
                    if (ch.equals(")")) {
                        nuevaLinea += " ) ";
                        continue;
                    }
                    if (ch.equals("{")) {
                        nuevaLinea += " { ";
                        continue;
                    }
                    if (ch.equals("}")) {
                        nuevaLinea += " } ";
                        continue;
                    }
                    if (ch.equals("[")) {
                        nuevaLinea += " [ ";
                        continue;
                    }
                    if (ch.equals("]")) {
                        nuevaLinea += " ] ";
                        continue;
                    }
                    if (ch.equals("<")) {
                        nuevaLinea += " < ";
                        continue;
                    }
                    if (ch.equals(">")) {
                        nuevaLinea += " > ";
                        continue;
                    }
                    if (ch.equals(".")) {
                        nuevaLinea += " . ";
                        continue;
                    }
                    if (ch.equals(",")) {
                        nuevaLinea += " , ";
                        continue;
                    }
                    if (ch.equals("!")) {
                        nuevaLinea += " ! ";
                        continue;
                    }
                    nuevaLinea += ch;
                    if (nuevaLinea.endsWith("--")) {
                        nuevaLinea = nuevaLinea.substring(0, nuevaLinea.length() - 2) + " -- ";
                        continue;
                    }
                    if (nuevaLinea.endsWith("++")) {
                        nuevaLinea = nuevaLinea.substring(0, nuevaLinea.length() - 2) + " ++ ";
                        continue;
                    }
                }
                linea = nuevaLinea;

                // tokenizo la linea por espacios
                var actualTokens = linea.trim().split("\\s+");

                var literal = "";

                for (String token : actualTokens) {
                    if (token.trim().isEmpty()) {
                        continue;
                    }

                    // validadas anteriormente
                    if (comentarioMultiLinea) {
                        if (token.endsWith("*/")) {
                            comentarioMultiLinea = false;
                        }
                        continue;
                    }

                    if (literal.length() > 0) {
                        literal += " " + token;
                        if (token.endsWith("\"")) {
                            addToken(tokens, new Token(literal, TokenType.LITERAL), semicolon);
                            literal = "";
                        }
                        continue;
                    }

                    // validaciones
                    if (token.startsWith("//")) {
                        break;
                    }

                    if (token.startsWith("/*")) {
                        comentarioMultiLinea = true;
                        break;
                    }

                    if (Token.reserved.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.RESERVADA), semicolon);
                        continue;
                    }

                    if (Token.operators.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.OPERADOR), semicolon);
                        continue;
                    }

                    if (Token.types.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.TIPO), semicolon);
                        continue;
                    }

                    if (Token.separators.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.SEPARADOR), semicolon);
                        continue;
                    }

                    if (token.startsWith("\"")) {
                        literal = "\"";
                        var ntoken = token.substring(1);
                        if (ntoken.endsWith("\"")) {
                            addToken(tokens, new Token(literal + ntoken, TokenType.LITERAL), semicolon);
                            literal = "";
                        } else {
                            literal += ntoken;
                        }
                        continue;
                    }

                    if (token.equals(".")) {
                        addToken(tokens, new Token(token, TokenType.SEPARADOR), semicolon);
                        continue;
                    }

                    if (token.matches("[0-9]+\\.?[0.9]*")) {
                        addToken(tokens, new Token(token, TokenType.LITERAL), semicolon);
                        continue;
                    }

                    if (token.matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
                        addToken(tokens, new Token(token, TokenType.IDENTIFICADOR), semicolon);
                        continue;
                    }

                    addToken(tokens, new Token(token, TokenType.DESCONOCIDO), semicolon);

                }
            
                tokens.add(new Token("\n", TokenType.EOF));
            }

        }
        return tokens;
    }

    public static Expression parseExpressions(ArrayList<Token> tokens, float minBp) {
        Expression lhs = null;
        // left hand side
        var ft = tokens.remove(0);
        if (ft == null) {
            throw new RuntimeException("Empty list");
        }
        if (ft.getValue().equals("(")) {
            lhs = parseExpressions(tokens, 0);
            if (!tokens.remove(0).getValue().equals(")")) {
                throw new RuntimeException("Expected )");
            }
        } else {
            if (!(ft.getType() == TokenType.LITERAL || ft.getType() == TokenType.IDENTIFICADOR)) {
                throw new RuntimeException("Expected " + TokenType.LITERAL + " or " + TokenType.IDENTIFICADOR + ", got " + ft.getType());
            }
        }        
        if (lhs == null) {
            lhs = new Expression(ft);
        }
        while (true) {
            // operator
            if (tokens.isEmpty()) {
                break;
            }
            var op = tokens.get(0);
            if (op.getType() == TokenType.EOF || op.getValue().equals(";") || op.getValue().equals(")")) {
                break;
            }
            if (!(op.getType() == TokenType.OPERADOR || op.getValue().equals(","))) {
                throw new RuntimeException("Expected operator, got " + op.getType());
            }
            // right hand side
            var bp = getBindingPower(op.getValue());
            if (bp.left() < minBp) {
                break;
            }
            tokens.remove(0);
            var rhs = parseExpressions(tokens, bp.right());
            lhs = new Expression(op, lhs, rhs);
        }
        return lhs;
    }

     static BindingPower getBindingPower(String op) {
        return switch (op) {
            case "," -> new BindingPower(1.0f, 1.1f);
            case "+" -> new BindingPower(2.0f, 2.1f);
            case "-" -> new BindingPower(2.0f, 2.1f);
            case "*" -> new BindingPower(3.0f, 3.1f);
            case "/" -> new BindingPower(3.0f, 3.1f);
            default -> new BindingPower(0, 0);
        };
    }

}

class BindingPower {
    private final float l;
    private final float r;

    public BindingPower(float left, float right) {
        this.l = left;
        this.r = right;
    }

    public float left() {
        return l;
    }

    public float right() {
        return r;
    }

}