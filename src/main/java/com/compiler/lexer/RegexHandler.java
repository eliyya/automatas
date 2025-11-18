package com.compiler.lexer;

import java.util.regex.Pattern;

@FunctionalInterface
public interface RegexHandler {
    void handle(Lexer lexer, Pattern pattern);
}