package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class BooleanExpression implements Expression {
    final String _c = "BooleanExpression";
    String value;

    public BooleanExpression(String value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
