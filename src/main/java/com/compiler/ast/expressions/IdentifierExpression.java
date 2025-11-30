package com.compiler.ast.expressions;

import com.compiler.lexer.Token;

public final class IdentifierExpression implements DeclarativeExpression {
    final String _c = "IdentifierExpression";
    Token value;

    public IdentifierExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.value();
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
