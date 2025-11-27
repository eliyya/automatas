package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class CharExpression implements Expression {
    final String _c = "CharExpression";
    char value;

    public CharExpression(char value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
