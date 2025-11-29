package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.lexer.Token;

public class NumberExpression implements Expression {
    final String _c = "NumberExpression";
    Token value;

    public NumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value.value();
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
