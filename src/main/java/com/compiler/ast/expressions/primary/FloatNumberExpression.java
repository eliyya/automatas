package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.lexer.Token;

public class FloatNumberExpression implements PrimaryExpression {
    String _c = "FloatNumberExpression";
    Token value;

    public FloatNumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}