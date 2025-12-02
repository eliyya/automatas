package com.compiler.ast.expressions.primary;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class BooleanExpression implements PrimaryExpression {
    final String _c = "BooleanExpression";
    Token value;

    public BooleanExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value.value();
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        if (type.kind() != TokenKind.BOOLEAN) {
            throw new InvalidTypeError(type, this.value);
        }
    }
    
}
