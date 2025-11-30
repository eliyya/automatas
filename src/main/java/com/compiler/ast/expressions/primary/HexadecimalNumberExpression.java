package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.lexer.Token;

public class HexadecimalNumberExpression implements PrimaryExpression {
    String _c = "HexadecimalNumberExpression";
    Token value;

    public HexadecimalNumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
