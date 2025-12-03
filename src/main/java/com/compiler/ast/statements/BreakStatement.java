package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.lexer.Token;

public class BreakStatement implements Statement {
    String _c = "BreakStatement";
    Token label;
    Token breakToken;

    public BreakStatement(Token label, Token breakToken) {
        this.label = label;
        this.breakToken = breakToken;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }

    @Override
    public String toString() {
        if (label == null) return "break;";
        else return "break " + label.value() + ";";
    }

    @Override
    public Token token() {
        return this.breakToken;
    }
    
}
