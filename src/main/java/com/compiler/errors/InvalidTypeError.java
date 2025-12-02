package com.compiler.errors;

import com.compiler.lexer.Token;
import com.compiler.utils.ConsoleColor;

public class InvalidTypeError extends RuntimeException {
    private static String format(String text, ConsoleColor color) {
        return ConsoleColor.format(text, color);
    }

    public InvalidTypeError(Token type, Token found) {
        IO.println("");
        IO.println(found.textLine());
        IO.println(" ".repeat(found.column()) + "^");
        super(
                format("AST:Error ->", ConsoleColor.RED)
                        + " Type of `" + format(found.value(), ConsoleColor.GREEN)
                        + "` is not valid type " + format(type.value(), ConsoleColor.GREEN));
    }
}

