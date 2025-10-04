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
            tokens.add(new Token(";", TokenType.SEPARATOR, token.getLine()));
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

    private static void tokenizeLine(String line, ArrayList<Token> tokens, boolean[] comentarioMultiLinea, int lineNum) {
        if (line.isEmpty()) {
            addToken(tokens, new Token("\n", TokenType.EOF, lineNum), false);
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
                    addToken(tokens, new Token(literal, TokenType.LITERAL, lineNum), semicolon);
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
                addToken(tokens, new Token(token, TokenType.RESERVED, lineNum), semicolon);
                continue;
            }

            if (Token.asignations.contains(token)) {
                addToken(tokens, new Token(token, TokenType.ASSIGNATION, lineNum), semicolon);
                continue;
            }

            if (Token.operators.contains(token)) {
                addToken(tokens, new Token(token, TokenType.OPERATOR, lineNum), semicolon);
                continue;
            }

            if (Token.types.contains(token)) {
                addToken(tokens, new Token(token, TokenType.TYPE, lineNum), semicolon);
                continue;
            }

            if (Token.separators.contains(token)) {
                addToken(tokens, new Token(token, TokenType.SEPARATOR, lineNum), semicolon);
                continue;
            }

            if (token.startsWith("\"")) {
                literal = "\"";
                var ntoken = token.substring(1);
                if (ntoken.endsWith("\"")) {
                    addToken(tokens, new Token(literal + ntoken, TokenType.LITERAL, lineNum), semicolon);
                    literal = "";
                } else {
                    literal += ntoken;
                }
                continue;
            }

            if (token.equals(".")) {
                addToken(tokens, new Token(token, TokenType.SEPARATOR, lineNum), semicolon);
                continue;
            }

            if (token.matches("[0-9]+\\.?[0.9]*")) {
                addToken(tokens, new Token(token, TokenType.LITERAL, lineNum), semicolon);
                continue;
            }

            if (token.matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
                addToken(tokens, new Token(token, TokenType.IDENTIFICATOR, lineNum), semicolon);
                continue;
            }

            addToken(tokens, new Token(token, TokenType.UNKNOWN, lineNum), semicolon);

        }

        tokens.add(new Token("\n", TokenType.EOF, lineNum));
    }

    public static ArrayList<Token> tokenizeFile(File file) throws FileNotFoundException {
        var tokens = new ArrayList<Token>();

        // leo un archivo y agrego las lineas al ArrayList
        try (var scanner = new Scanner(file)) {
            boolean[] comentarioMultiLinea = {false};
            int lineNum = 1;
            while (scanner.hasNext()) {
                var linea = normalizeLine(scanner.nextLine().trim());
                tokenizeLine(linea, tokens, comentarioMultiLinea, lineNum);
                lineNum++;
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
                throw new SyntaxError(")", tokens.remove(0).getValue(), ft.getLine());
            }
        } else {
            if (!(ft.getType() == TokenType.LITERAL || ft.getType() == TokenType.IDENTIFICATOR)) {
                throw new SyntaxError(TokenType.LITERAL.name(), ft.getType().name(), ft.getLine());
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
            if (!(op.getType() == TokenType.OPERATOR || op.getValue().equals(",") || Token.asignations.contains(op.getValue()))) {
                throw new SyntaxError(TokenType.OPERATOR.name(), op.getValue(), op.getLine());
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

    public Analizer(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public static Node analize(ArrayList<Token> tokens) {        
        if (tokens.get(0).getType() == TokenType.TYPE || tokens.get(0).getType() == TokenType.IDENTIFICATOR) {
            return parseDeclaration(tokens);
        } else return parseExpression(tokens, 0);
    }

    static Declaration parseDeclaration(ArrayList<Token> tokens) {
        var type = tokens.remove(0);
        if (!(type.getType() == TokenType.TYPE || type.getType() == TokenType.IDENTIFICATOR)) {
            throw new SyntaxError(TokenType.TYPE.name(), type.getType().name(), type.getLine());
        }
        var expression = parseExpression(tokens, 0);
        return new Declaration(type, expression);
    }
}
