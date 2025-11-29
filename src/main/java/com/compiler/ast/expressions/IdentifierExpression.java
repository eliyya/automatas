package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class IdentifierExpression implements Expression {
    final String _c = "IdentifierExpression";
    String value;

    public IdentifierExpression(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
