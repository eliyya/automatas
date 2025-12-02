package com.compiler.errors;

import com.compiler.lexer.Token;
import com.compiler.utils.Console;
import com.compiler.utils.ConsoleColor;

public class UnresolvedError extends RuntimeException {
    public UnresolvedError(Token token) {
        IO.println("");
        IO.println(token.textLine());
        IO.println(" ".repeat(token.column()) + "^");
        super(Console.format("Parser:Error ->", ConsoleColor.RED) + " Unresolved variable: " + token.value());
    }
}
