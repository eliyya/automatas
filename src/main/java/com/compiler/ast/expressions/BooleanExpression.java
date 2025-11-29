package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class BooleanExpression implements Expression {
    final String _c = "BooleanExpression";
    boolean value;

    public BooleanExpression(boolean  value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
