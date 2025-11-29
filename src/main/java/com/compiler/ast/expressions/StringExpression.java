package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class StringExpression implements Expression {
    final String _c = "StringExpression";
    String value;

    public StringExpression(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
