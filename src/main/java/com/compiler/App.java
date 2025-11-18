package com.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;
import com.compiler.parser.Parser;
import com.google.gson.Gson;

public class App {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No se proporcionó ningún archivo. especifique la ruta del archivo como argumento.");
            return;
        }
        var source = Files.readString(Path.of(args[0]));
        var tokens = Lexer.tokenize(source);
        printTokens(tokens);
        var ast = Parser.parse(tokens); 
        Gson gson = new Gson();
        System.out.println(gson.toJson(ast));
    }

    private static void printTokens(List<Token> tokens) {
        for (var token : tokens) {
            if (token.kind() == TokenKind.STRING
                    || token.kind() == TokenKind.NUMBER
                    || token.kind() == TokenKind.CHAR
                    || token.kind() == TokenKind.IDENTIFIER) {
                System.out.println(token.kind().toString() + " (" + token.value() + ")");
            } else {
                System.out.println(token.kind().toString());
            }
        }
    }
}
