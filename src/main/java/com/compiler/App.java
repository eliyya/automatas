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
            IO.println("----------------------------------------------");
            IO.println(format("No se proporcionó ningún archivo", ConsoleColor.RED));
            IO.println("especifique la ruta del archivo como argumento");
            IO.println(
                    "Ejemplo: " + format("java", ConsoleColor.YELLOW) + format(" -jar", ConsoleColor.BLACK_BRIGHT)
                            + " App.jar " + format("test.java.txt", ConsoleColor.BLUE_UNDERLINED));
            IO.println("----------------------------------------------");
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
        printTokens(tokens);
        printAST(ast);
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
                IO.println(token.kind().toString() + " (" + token.value() + ") [" + token.line() + ":"
                        + token.column() + "]");
            } else {
                IO.println(token.kind().toString() + " [" + token.line() + ":" + token.column() + "]");
            }
        }
    }

    private static void printAST(Statment ast) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        IO.println(gson.toJson(ast));
    }

    private static void writeTokens(List<Token> tokens) {
        try (var writer = new FileWriter("tokens.log")) {
            for (var token : tokens) {
                writer.write(token.kind().toString()
                        + " (" + token.value() + ") "
                        + "[" + token.line() + ":" + token.column() + "]\n");
            }
            IO.println("");
            IO.println("--------------------------------------------------------");
            IO.println(
                    "Se han escrito los " + format("tokens", ConsoleColor.GREEN_BOLD) + " para su análisis en "
                            + format("tokens.log", ConsoleColor.BLUE_UNDERLINED));
            IO.println("Contiene por cada token en una linea:");
            IO.println("  - el tipo de token " + format("TOKEN_TYPE", ConsoleColor.WHITE_BOLD_BRIGHT));
            IO.println("  - su valor entre paréntesis " + format("(", ConsoleColor.WHITE_BOLD_BRIGHT)
                    + format("value", ConsoleColor.GREEN_BOLD) + format(")", ConsoleColor.WHITE_BOLD_BRIGHT));
            IO.println("  - su posición en el archivo " + format("[", ConsoleColor.WHITE_BOLD_BRIGHT)
                    + format("linea", ConsoleColor.BLUE_BOLD) + format(":", ConsoleColor.WHITE_BOLD_BRIGHT)
                    + format("columna", ConsoleColor.BLUE_BOLD) + format("]", ConsoleColor.WHITE_BOLD_BRIGHT));
            IO.println("--------------------------------------------------------");
            IO.println("");
        } catch (IOException e) {
            IO.println("");
            IO.println("Error al escribir tokens");
            IO.println("");
        }
    }

    private static void writeAST(Statment ast) {
        try (var writer = new FileWriter("ast.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            writer.write(gson.toJson(ast));
            IO.println("");
            IO.println("--------------------------------------------------------");
            IO.println(
                    "Se ha generado el " + format("Abstract Syntax Tree", ConsoleColor.GREEN) + " ("
                            + format("AST", ConsoleColor.GREEN_BOLD) + ") en "
                            + format("ast.json", ConsoleColor.BLUE_UNDERLINED));
            IO.println("--------------------------------------------------------");
            IO.println("");
        } catch (IOException e) {
            IO.println("");
            IO.println("Error al escribir AST");
            IO.println("");
        }
    }

    private static void writeASTTree(Statment ast) {
        try (var writer = new FileWriter("tree.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            var tree = gson.toJson(ast).replaceAll("\"_c\"", "\"\"");
            writer.write(tree);
            IO.println("");
            IO.println("--------------------------------------------------------");
            IO.println(
                    "Se ha escrito un archivo especial basado en el "
                            + format("ast.json", ConsoleColor.BLUE_UNDERLINED));
            IO.println("en " + format("tree.json", ConsoleColor.BLUE_UNDERLINED)
                    + " para su análisis de manera visual creado");
            IO.println(
                    "para la herramienta online: "
                            + format("https://jsoncrack.com/editor", ConsoleColor.BLUE_UNDERLINED));
            IO.println("Solamente necesita:");
            IO.println("  - abrir la herramienta online");
            IO.println("  - dirigirse a " + format("File", ConsoleColor.BLUE) + " -> "
                    + format("Import", ConsoleColor.BLUE));
            IO.println(
                    "  - arrastrar el archivo " + format("tree.json", ConsoleColor.BLUE_UNDERLINED) + " a la herramienta");
            IO.println("--------------------------------------------------------");
            IO.println("");
        } catch (IOException e) {
            IO.println("");
            IO.println("Error al escribir AST Tree");
            IO.println("");
        }
    }

    private static String format(String text, ConsoleColor color) {
        return ConsoleColor.format(text, color);
    }
}
