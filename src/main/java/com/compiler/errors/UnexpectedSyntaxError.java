package com.compiler.errors;

import com.compiler.lexer.Token;
import com.compiler.utils.ConsoleColor;

public class UnexpectedSyntaxError extends RuntimeException {
    public UnexpectedSyntaxError(Token token) {
        IO.println("");
        IO.println(token.textLine());
        IO.println(" ".repeat(token.column()) + "^");
        super(ConsoleColor.format("Parser:Error ->", ConsoleColor.RED) + " Unexpected syntax");
    }
}
