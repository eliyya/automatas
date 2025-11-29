package com.compiler.errors;

import com.compiler.ConsoleColor;
import com.compiler.lexer.Token;
import com.compiler.parser.Parser;

public class ExpectedError extends RuntimeException {
    private static String format(String text, ConsoleColor color) {
        return ConsoleColor.format(text, color);
    }

    public ExpectedError(Parser parser, String expected, Token found) {
        System.out.println("");
        var line = parser.lines.get(found.line() - 1);
        System.out.println(line);
        System.out.println(" ".repeat(found.column()) + "^");
        super(
                format("Parser:Error ->", ConsoleColor.RED)
                        + " Expected `" + format(expected, ConsoleColor.GREEN)
                        + "` but found `" + format(found.value(), ConsoleColor.GREEN)
                        + "` at " + format("line " + found.line(), ConsoleColor.GREEN)
                        + " and " + format("column " + found.column(), ConsoleColor.GREEN));
    }
}
