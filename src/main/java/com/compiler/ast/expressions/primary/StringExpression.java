package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.lexer.Token;

public class StringExpression implements PrimaryExpression {
    final String _c = "StringExpression";
    Token value;

    public StringExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value.value() + "\"";
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
