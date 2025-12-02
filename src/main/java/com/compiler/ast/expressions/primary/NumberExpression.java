package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class NumberExpression implements PrimaryExpression {
    final String _c = "NumberExpression";
    Token value;

    public NumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.value();
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        System.out.println(type);
        if (type.kind() == TokenKind.FLOAT) {
            if (!this.isValidFloat()) {
                throw new IllegalArgumentException("Invalid float " + this.value.value());
            }
            return;
        }
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }

    private boolean isValidFloat() {
        return (value.value().endsWith("f") || value.value().endsWith("F"));
    }
    
}
