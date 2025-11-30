package com.compiler.errors;

import com.compiler.ConsoleColor;
import com.compiler.lexer.Token;
import com.compiler.parser.Parser;

public class UnexpectedSyntaxError extends RuntimeException {
    public UnexpectedSyntaxError(Parser parser, Token token) {
        IO.println("");
        var line = parser.lines.get(token.line() - 1);
        IO.println(line);
        IO.println(" ".repeat(token.column()) + "^");
        super(ConsoleColor.format("Parser:Error ->", ConsoleColor.RED) + " Unexpected syntax");
    }
}
