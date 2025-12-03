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
        if (type.kind() == TokenKind.OBJECT) {
            return;
        }
        if (type.kind() != TokenKind.BOOLEAN && type.kind() != TokenKind.VAR && type.kind() != TokenKind.OBJECT) {
            throw new InvalidTypeError(type, this.value);
        }
    }

    @Override
    public Token getToken() {
        return this.value;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        return true;
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        return false;
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return true;
    }
}
