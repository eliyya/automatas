package com.compiler;

import com.compiler.utils.TokenType;

public record Token(String value, TokenType type, int line, int column, String original) {}
