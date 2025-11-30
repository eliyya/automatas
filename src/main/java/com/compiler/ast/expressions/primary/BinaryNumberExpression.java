package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.lexer.Token;

public class BinaryNumberExpression implements PrimaryExpression {
    final String _c = "BinaryNumberExpression";
    Token value;

    public BinaryNumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
