package com.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;
import com.compiler.parser.Parser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("----------------------------------------------");
            System.out.println(format("No se proporcionó ningún archivo", ConsoleColor.RED));
            System.out.println("especifique la ruta del archivo como argumento");
            System.out.println(
                    "Ejemplo: " + format("java", ConsoleColor.YELLOW) + format(" -jar", ConsoleColor.BLACK_BRIGHT)
                            + " App.jar " + format("test.java.txt", ConsoleColor.BLUE_UNDERLINED));
            System.out.println("----------------------------------------------");
            return;
        }

        var source = Files.readString(Path.of(args[0]));
        // --------------------
        // lexer - tokenization
        // --------------------
        var tokens = Lexer.tokenize(source);
        var lines = source.lines().toList();
        // ----------------
        // parser - parsing
        // ----------------
        var ast = Parser.parse(tokens, lines);
        // ----------------
        // print - printing
        // ----------------
        // printTokens(tokens);
        // printAST(ast);
        // ----------------
        // write - writing
        // ----------------
        writeTokens(tokens);
        writeAST(ast);
        writeASTTree(ast);
    }

    private static void printTokens(List<Token> tokens) {
        for (var token : tokens) {
            if (token.kind() == TokenKind.STRING_EXPRESSION
                    || token.kind() == TokenKind.NUMBER_EXPRESSION
                    || token.kind() == TokenKind.CHAR
                    || token.kind() == TokenKind.IDENTIFIER) {
                System.out.println(token.kind().toString() + " (" + token.value() + ") [" + token.line() + ":"
                        + token.column() + "]");
            } else {
                System.out.println(token.kind().toString() + " [" + token.line() + ":" + token.column() + "]");
            }
        }
    }

    private static void printAST(Statment ast) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        System.out.println(gson.toJson(ast));
    }

    private static void writeTokens(List<Token> tokens) {
        try (var writer = new FileWriter("tokens.log")) {
            for (var token : tokens) {
                writer.write(token.kind().toString()
                        + " (" + token.value() + ") "
                        + "[" + token.line() + ":" + token.column() + "]\n");
            }
            System.out.println("");
            System.out.println("--------------------------------------------------------");
            System.out.println(
                    "Se han escrito los " + format("tokens", ConsoleColor.GREEN_BOLD) + " para su análisis en "
                            + format("tokens.log", ConsoleColor.BLUE_UNDERLINED));
            System.out.println("Contiene por cada token en una linea:");
            System.out.println("  - el tipo de token " + format("TOKEN_TYPE", ConsoleColor.WHITE_BOLD_BRIGHT));
            System.out.println("  - su valor entre paréntesis " + format("(", ConsoleColor.WHITE_BOLD_BRIGHT)
                    + format("value", ConsoleColor.GREEN_BOLD) + format(")", ConsoleColor.WHITE_BOLD_BRIGHT));
            System.out.println("  - su posición en el archivo " + format("[", ConsoleColor.WHITE_BOLD_BRIGHT)
                    + format("linea", ConsoleColor.BLUE_BOLD) + format(":", ConsoleColor.WHITE_BOLD_BRIGHT)
                    + format("columna", ConsoleColor.BLUE_BOLD) + format("]", ConsoleColor.WHITE_BOLD_BRIGHT));
            System.out.println("--------------------------------------------------------");
            System.out.println("");
        } catch (IOException e) {
            System.out.println("");
            System.out.println("Error al escribir tokens");
            System.out.println("");
        }
    }

    private static void writeAST(Statment ast) {
        try (var writer = new FileWriter("ast.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            writer.write(gson.toJson(ast));
            System.out.println("");
            System.out.println("--------------------------------------------------------");
            System.out.println(
                    "Se ha generado el " + format("Abstract Syntax Tree", ConsoleColor.GREEN) + " ("
                            + format("AST", ConsoleColor.GREEN_BOLD) + ") en "
                            + format("ast.json", ConsoleColor.BLUE_UNDERLINED));
            System.out.println("--------------------------------------------------------");
            System.out.println("");
        } catch (IOException e) {
            System.out.println("");
            System.out.println("Error al escribir AST");
            System.out.println("");
        }
    }

    private static void writeASTTree(Statment ast) {
        try (var writer = new FileWriter("tree.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            var tree = gson.toJson(ast).replaceAll("\"_c\"", "\"\"");
            writer.write(tree);
            System.out.println("");
            System.out.println("--------------------------------------------------------");
            System.out.println(
                    "Se ha escrito un archivo especial basado en el "
                            + format("ast.json", ConsoleColor.BLUE_UNDERLINED));
            System.out.println("en " + format("tree.json", ConsoleColor.BLUE_BOLD)
                    + " para su análisis de manera visual creado");
            System.out.println(
                    "para la herramienta online: "
                            + format("https://jsoncrack.com/editor", ConsoleColor.BLUE_UNDERLINED));
            System.out.println("Solamente necesita:");
            System.out.println("  - abrir la herramienta online");
            System.out.println("  - dirigirse a " + format("File", ConsoleColor.BLUE) + " -> "
                    + format("Import", ConsoleColor.BLUE));
            System.out.println(
                    "  - arrastrar el archivo " + format("tree.json", ConsoleColor.BLUE_BOLD) + " a la herramienta");
            System.out.println("--------------------------------------------------------");
            System.out.println("");
        } catch (IOException e) {
            System.out.println("");
            System.out.println("Error al escribir AST Tree");
            System.out.println("");
        }
    }

    private static String format(String text, ConsoleColor color) {
        return color + text + ConsoleColor.RESET;
    }
}
