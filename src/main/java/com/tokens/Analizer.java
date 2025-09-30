package com.tokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Analizer {
    private ArrayList<Token> tokens;

    private static void addToken(ArrayList<Token> tokens, Token token, boolean semicolon) {
        tokens.add(token);
        if (semicolon) {
            tokens.add(new Token(";", TokenType.SEPARADOR));
        }
    }

    private static String normalizeLine(String line) {
        var nuevaLinea = "";
        var lit = false;
        for (String ch : line.split("")) {
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
        return nuevaLinea;
    }

    private static void tokenizeLine(String line, ArrayList<Token> tokens, boolean[] comentarioMultiLinea) {
        if (line.isEmpty()) {
            addToken(tokens, new Token("\n", TokenType.EOF), false);
            return;
        }

        var literal = "";
        var semicolon = false;
        for (String token : line.trim().split("\\s+")) {
            if (token.trim().isEmpty()) {
                continue;
            }

            // validadas anteriormente
            if (comentarioMultiLinea[0]) {
                if (token.endsWith("*/")) {
                    comentarioMultiLinea[0] = false;
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
                comentarioMultiLinea[0] = true;
                break;
            }

            if (Token.reserved.contains(token)) {
                addToken(tokens, new Token(token, TokenType.RESERVADA), semicolon);
                continue;
            }

            if (Token.asignations.contains(token)) {
                addToken(tokens, new Token(token, TokenType.ASIGNACION), semicolon);
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

    public static ArrayList<Token> tokenizeFile(File file) throws FileNotFoundException {
        var tokens = new ArrayList<Token>();

        // leo un archivo y agrego las lineas al ArrayList
        try (var scanner = new Scanner(file)) {
            boolean[] comentarioMultiLinea = {false};
            while (scanner.hasNext()) {
                var linea = normalizeLine(scanner.nextLine().trim());
                tokenizeLine(linea, tokens, comentarioMultiLinea);
            }

        }
        return tokens;
    }

    public static Expression parseExpression(ArrayList<Token> tokens) {
        return parseExpression(tokens, 0);
    }

    public static Expression parseExpression(ArrayList<Token> tokens, float minBp) {
        Expression lhs = null;
        // left hand side
        var ft = tokens.remove(0);
        if (ft == null) {
            throw new RuntimeException("Empty list");
        }
        if (ft.getValue().equals("(")) {
            lhs = parseExpression(tokens, 0);
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
            if (!(op.getType() == TokenType.OPERADOR || op.getValue().equals(",") || Token.asignations.contains(op.getValue()))) {
                throw new RuntimeException("Expected operator, got " + op.getType());
            }
            // right hand side
            var bp = getBindingPower(op.getValue());
            if (bp.left() < minBp) {
                break;
            }
            tokens.remove(0);
            var rhs = parseExpression(tokens, bp.right());
            lhs = new Expression(op, lhs, rhs);
        }
        return lhs;
    }

    static BindingPower getBindingPower(String op) {
        var normalized = normalizeOperator(op);
        return switch (normalized) {
            case "," ->
                new BindingPower(1.0f, 1.1f);
            case "op" ->
                new BindingPower(2.0f, 2.1f);
            case "+" ->
                new BindingPower(3.0f, 3.1f);
            case "-" ->
                new BindingPower(3.0f, 2.1f);
            case "*" ->
                new BindingPower(4.0f, 4.1f);
            case "/" ->
                new BindingPower(4.0f, 4.1f);
            default ->
                new BindingPower(0, 0);
        };
    }

    static String normalizeOperator(String op) {
        if (Token.asignations.contains(op)) {
            return "op";
        }
        return op;
    }

    public static Expression parseAsignation(ArrayList<Token> tokens, ArrayList<Token> identificators) {
        var name = tokens.get(0);
        if (name.getType() != TokenType.IDENTIFICADOR) {
            throw new RuntimeException("Expected identifier, got " + name.getValue());
        }
        tokens.remove(0);
        var operator = tokens.get(0);
        if (operator.getType() != TokenType.ASIGNACION) {
            throw new RuntimeException("Expected =, got " + operator.getValue());
        }
        tokens.remove(0);
        var value = parseExpression(tokens, 0);
        identificators.add(name);
        return new Expression(operator, new Expression(name), value); 
    }

    public Analizer(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    void analize() {
        if (this.tokens.get(0).getType() == TokenType.TIPO) {
            parseDeclaration(tokens);
        }
    }

    static void parseDeclaration(ArrayList<Token> tokens) {
        var type = tokens.remove(0);
        if (!(type.getType() == TokenType.TIPO || type.getType() == TokenType.IDENTIFICADOR)) {
            throw new RuntimeException("Expected type, got " + type.getValue());
        }
        var name = tokens.remove(0);
        if (name.getType() != TokenType.IDENTIFICADOR) {
            throw new RuntimeException("Expected identifier, got " + name.getValue());
        }
        var next = tokens.remove(0);
        if (!(next.getValue().equals(";") || next.getValue().equals("=") || next.getValue().equals(","))) {
            throw new RuntimeException("Expected ;, got " + next.getValue());
        }
        if (next.getValue().equals(";")) {
            return;
        }
        if (next.getValue().equals(",")) {
            parseDeclaration(tokens, type);
        }
    }

    static void parseDeclaration(ArrayList<Token> tokens, Token type) {
        var name = tokens.remove(0);
        if (name.getType() != TokenType.IDENTIFICADOR) {
            throw new RuntimeException("Expected identifier, got " + name.getValue());
        }
        var next = tokens.remove(0);
        if (!(next.getValue().equals(";") || next.getValue().equals("=") || next.getValue().equals(","))) {
            throw new RuntimeException("Expected ;, got " + next.getValue());
        }
        if (next.getValue().equals(";")) {
            return;
        }
        if (next.getValue().equals(",")) {
            parseDeclaration(tokens, type);
        }
    }
}
