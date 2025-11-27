package com.compiler.lexer;

public record Token(TokenKind kind, String value, int line, int column) {

}
