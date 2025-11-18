package com.compiler.lexer;

import java.util.regex.Pattern;

public interface RegexHandler {
    void handle(Lexer lexer, Pattern pattern);
}