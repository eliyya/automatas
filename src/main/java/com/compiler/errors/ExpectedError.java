package com.compiler.errors;

import com.compiler.lexer.Token;
import com.compiler.parser.Parser;

public class ExpectedError extends RuntimeException {
    public ExpectedError(Parser parser, String expected, Token found) {
        System.out.println("");
        var line = parser.lines.get(found.line() - 1);
        System.out.println(line);
        System.out.println(" ".repeat(found.column()) + "^");
        super("\u001B[31mParser:Error ->\u001B[0m Expected `\u001B[32m" + expected + "\u001B[0m` but found `\u001B[32m" + found.value() + "\u001B[0m` at \u001B[34mline "
                + found.line() + "\u001B[0m and \u001B[34mcolumn " + found.column()+"\u001B[0m");
    }
}
