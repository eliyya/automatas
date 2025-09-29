package com.tokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Token {

    public static HashSet<String> reservadas = new HashSet<>(
            List.of(
                    "abstract", "assert", "break", "case", "catch", "class", "const", "continue", "default",
                    "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if", "implements", "import",
                    "instanceof", "interface", "native", "new", "package", "private", "protected", "public", "return",
                    "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
                    "try", "void", "volatile", "while", "true", "false", "null", "var", "yield", "record"));

    public static HashSet<String> tipos = new HashSet<>(
            List.of("int", "long", "float", "double", "boolean", "char", "byte", "short", "void"));

    public static HashSet<String> operadores = new HashSet<>(
            List.of(
                    // ternarios
                    "?", ":",
                    // logicos ternarios
                    "&&", "||", "!",
                    // aritmeticos
                    "+", "-", "*", "/", "%", "++", "--",
                    // relacionales
                    "==", "!=", ">", "<", ">=", "<=",
                    // bitwise
                    "&", "|", "^", "~", "<<", ">>",
                    // asignacion
                    "=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>="));

    public static HashSet<String> separadores = new HashSet<>(
            List.of("(", ")", "{", "}", "[", "]", ";", ",", "."));

    private final String valor;
    private final String tipo;

    public Token(String valor, String tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

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

                    if (Token.reservadas.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.RESERVADA), semicolon);
                        continue;
                    }

                    if (Token.operadores.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.OPERADOR), semicolon);
                        continue;
                    }

                    if (Token.tipos.contains(token)) {
                        addToken(tokens, new Token(token, TokenType.TIPO), semicolon);
                        continue;
                    }

                    if (Token.separadores.contains(token)) {
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
            }
        }
        return tokens;
    }
}
