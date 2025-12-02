package com.compiler.errors;

import com.compiler.lexer.Token;
import com.compiler.utils.ConsoleColor;

public class ExpectedError extends RuntimeException {
    private static String format(String text, ConsoleColor color) {
        return ConsoleColor.format(text, color);
    }

    public ExpectedError(String expected, Token found) {
        IO.println("");
        IO.println(found.textLine());
        IO.println(" ".repeat(found.column()) + "^");
        super(
                format("Parser:Error ->", ConsoleColor.RED)
                        + " Expected `" + format(expected, ConsoleColor.GREEN)
                        + "` but found `" + format(found.value(), ConsoleColor.GREEN)
                        + "` at " + format("line " + found.line(), ConsoleColor.GREEN)
                        + " and " + format("column " + found.column(), ConsoleColor.GREEN));
    }
}
