package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class StringExpression implements Expression {
    String value;

    public StringExpression(String value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
