package com.tokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Tokens {

    public static void main(String[] args) {
        var lineas = new ArrayList<String>();
        var tokens = new ArrayList<Token>();

        try {
            // leo un archivo y agrego las lineas al ArrayList
            try (var file = new Scanner(new File("./src/main/java/com/tokens/Token.java"))) {
                while (file.hasNext())
                    lineas.add(file.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo.");
        }

        var comentarioMultiLinea = false;
        var semicolon = false;
        // recorro el ArrayList
        for (var linea : lineas) {

            if (semicolon) {
                tokens.add(new Token(";", TokenType.SEPARADOR));
                semicolon = false;
            }
            // tokenizo la linea por espacios
            var actualTokens = linea.trim().split("\\s+");

            var comentario = false;
            var literal = "";

            for (String token : actualTokens) {
                if (token.isEmpty())
                    continue;

                if (token.endsWith(";")) {
                    token = token.substring(0, token.length() - 1);
                    semicolon = true;
                }

                // validadas anteriormente
                if (comentarioMultiLinea) {
                    tokens.add(new Token(token, TokenType.COMENTARIO));
                    if (token.endsWith("*/"))
                        comentarioMultiLinea = false;
                    continue;
                }

                if (comentario) {
                    tokens.add(new Token(token, TokenType.COMENTARIO));
                    continue;
                }

                if (literal.length() > 0) {
                    literal += " " + token;
                    if (token.endsWith("\"")) {
                        tokens.add(new Token(literal, TokenType.LITERAL));
                        literal = "";
                    }
                    continue;
                }

                // validaciones

                if (token.startsWith("//")) {
                    comentario = true;
                    tokens.add(new Token(token, TokenType.COMENTARIO));
                    continue;
                }

                if (token.startsWith("/*")) {
                    comentarioMultiLinea = true;
                    tokens.add(new Token(token, TokenType.COMENTARIO));
                    continue;
                }

                if (Token.reservadas.contains(token)) {
                    tokens.add(new Token(token, TokenType.RESERVADA));
                    continue;
                }

                if (Token.operadores.contains(token)) {
                    tokens.add(new Token(token, TokenType.OPERADOR));
                    continue;
                }

                if (Token.tipos.contains(token)) {
                    tokens.add(new Token(token, TokenType.TIPO));
                    continue;
                }

                if (Token.separadores.contains(token)) {
                    tokens.add(new Token(token, TokenType.SEPARADOR));
                    continue;
                }

                if (token.startsWith("\"")) {
                    literal = token;
                    if (token.endsWith("\"")) {
                        tokens.add(new Token(literal, TokenType.LITERAL));
                        literal = "";
                    }
                    continue;
                }

                if (token.equals(".")) {
                    tokens.add(new Token(token, TokenType.SEPARADOR));
                    continue;
                }

                if (token.matches("[0-9]\\.?[0.9]*")) {
                    tokens.add(new Token(token, TokenType.LITERAL));
                    continue;
                }

                if (token.contains(".")) {
                    var partes = token.split("\\.");
                    for (var i = 0; i < partes.length; i++) {
                        var parte = partes[i];

                        if (parte.isEmpty())
                            continue;

                        if (parte.matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
                            tokens.add(new Token(parte, TokenType.IDENTIFICADOR));

                            if (i < partes.length - 1)
                                tokens.add(new Token(".", TokenType.SEPARADOR));

                            continue;
                        }

                        tokens.add(new Token(parte, TokenType.DESCONOCIDO));
                    }
                    continue;
                }

                if (token.matches("[a-zA-Z_$][a-zA-Z_$0-9]*")) {
                    tokens.add(new Token(token, TokenType.IDENTIFICADOR));
                    continue;
                }

                tokens.add(new Token(token, TokenType.DESCONOCIDO));
            }
        }

        System.out.println("Tokens encontrados: " + tokens.size());
        System.out.println();
        for (var token : tokens)
            System.out.println("Tipo: "
                    + (token.getTipo().equals(TokenType.DESCONOCIDO) ? "\u001B[31m" + token.getTipo() + "\u001B[0m"
                            : token.getTipo())
                    + " ".repeat(15 - token.getTipo().length()) + "Valor: "
                    + token.getValor());
    }
}
