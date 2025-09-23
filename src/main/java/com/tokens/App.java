// C--
package com.tokens;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * este es un comentario de varias lineas
 */
public class App {

    static void addToken(ArrayList<Token> tokens, Token token, boolean semicolon) {
        tokens.add(token);
        if (semicolon) {
            tokens.add(new Token(";", TokenType.SEPARADOR));
        }
    }

    public static void main(String[] args) throws IOException {
        var tokens = new ArrayList<Token>();
        if (args.length == 0) {
            System.out.println("No se proporcionó ningún archivo.");
            return;
        }

        try {
            // leo un archivo y agrego las lineas al ArrayList
            try (var file = new Scanner(new File(args[0]))) {
                while (file.hasNext()) {
                    // recorro el ArrayList
                    var linea = file.nextLine().trim();

                    var comentarioMultiLinea = false;
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
                            continue;
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

                    System.out.println("Tokens encontrados: " + tokens.size());
                    System.out.println();
                    for (var token : tokens) {
                        System.out.println("Tipo: "
                                + (
                                    token.getTipo().equals(TokenType.DESCONOCIDO) ? ConsoleColors.ANSI_RED + token.getTipo() + ConsoleColors.ANSI_RESET
                                    : token.getTipo().equals(TokenType.LITERAL) ? ConsoleColors.ANSI_GREEN + token.getTipo() + ConsoleColors.ANSI_RESET
                                    : token.getTipo())
                                + " ".repeat(15 - token.getTipo().length()) + "Valor: "
                                + token.getValor());
                    }
                    // write tokens to file
                    try (var log = new FileWriter("tokens.txt")) {
                        for (var token : tokens) {
                            log.write("Tipo: "
                                + token.getTipo()
                                + " ".repeat(15 - token.getTipo().length()) + "Valor: "
                                + token.getValor() + "\n");
                        }
                    } catch (IOException e) {
                        System.out.println("No se pudo escribir el archivo.");
                    }
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontró el archivo.");
        }
    }
}
