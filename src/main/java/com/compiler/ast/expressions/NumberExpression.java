package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public class NumberExpression implements Expression {
    float value;

    public NumberExpression(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NumberExpression{" + "value=" + value + '}';
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
