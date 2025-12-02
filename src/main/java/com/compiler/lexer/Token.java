package com.compiler.lexer;

import com.compiler.utils.JsonIgnore;

public class Token {
    private TokenKind kind;
    private String value;
    @JsonIgnore
    private int line;
    @JsonIgnore
    private int column;

    public Token(TokenKind kind, String value, int line, int column) {
        this.kind = kind;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenKind kind() {
        return this.kind;
    }

    public String value() {
        return this.value;
    }

    public int line() {
        return this.line;
    }

    public int column() {
        return this.column;
    }

    public String toString() {
        return "Token{" + "kind=" + kind + ", value='" + value + '\'' + ", line=" + line + ", column=" + column + '}';
    }
    
}
