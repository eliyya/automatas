package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.errors.UnresolvedError;
import com.compiler.lexer.Token;

public class ContinueStatement implements Statement {
    Token label;
    Token breakToken;

    public ContinueStatement(Token label, Token breakToken) {
        this.label = label;
        this.breakToken = breakToken;
    }

    @Override
    public void validate(BlockStatement parent) {
        if (this.label != null) {
            if (!parent.labels.contains(this.label.value())) {
                throw new UnresolvedError(this.label);
            }
        }
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
