package com.compiler.lexer;

import com.compiler.utils.JsonIgnore;

public class Token {
    private final TokenKind kind;
    private final String value;
    @JsonIgnore
    private final int line;
    @JsonIgnore
    private final int column;
    @JsonIgnore
    private final String textLine;

    public Token(TokenKind kind, String value, int line, int column, String textLine) {
        this.kind = kind;
        this.value = value;
        this.line = line;
        this.column = column;
        this.textLine = textLine;
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

    public String textLine() {
        return this.textLine;
    }

    @Override
    public String toString() {
        return "Token{" + "kind=" + kind + ", value='" + value + '\'' + ", line=" + line + ", column=" + column + '}';
    }
    
}
