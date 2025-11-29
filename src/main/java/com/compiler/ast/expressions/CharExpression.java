package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class CharExpression implements Expression {
    final String _c = "CharExpression";
    String value;

    public CharExpression(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
