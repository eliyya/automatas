package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.lexer.Token;

public class OctalNumberExpression implements PrimaryExpression {
    final String _c = "OctalNumberExpression";
    Token value;

    public OctalNumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
