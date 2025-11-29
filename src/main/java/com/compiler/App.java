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
            System.out.println("No se proporcionó ningún archivo. especifique la ruta del archivo como argumento.");
            return;
        }

        var source = Files.readString(Path.of(args[0]));
        // --------------------
        // lexer - tokenization
        // --------------------
        var tokens = Lexer.tokenize(source);
        var lines = source.lines().toList();
        printTokens(tokens);
        writeTokens(tokens);
        // ----------------
        // parser - parsing
        // ----------------
        var ast = Parser.parse(tokens, lines);
        printAST(ast);
        writeAST(ast);
    }

    private static void printTokens(List<Token> tokens) {
        for (var token : tokens) {
            if (token.kind() == TokenKind.STRING_EXPRESSION
                    || token.kind() == TokenKind.NUMBER_EXPRESSION
                    || token.kind() == TokenKind.CHAR
                    || token.kind() == TokenKind.IDENTIFIER) {
                System.out.println(token.kind().toString() + " (" + token.value() + ")");
            } else {
                System.out.println(token.kind().toString());
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
                if (token.kind() == TokenKind.STRING_EXPRESSION
                        || token.kind() == TokenKind.NUMBER_EXPRESSION
                        || token.kind() == TokenKind.CHAR
                        || token.kind() == TokenKind.IDENTIFIER) {
                    writer.write(token.kind().toString() + " (" + token.value() + ")\n");
                } else {
                    writer.write(token.kind().toString() + "\n");
                }
            }
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
        } catch (IOException e) {
            System.out.println("");
            System.out.println("Error al escribir AST");
            System.out.println("");
        }
    }
}
