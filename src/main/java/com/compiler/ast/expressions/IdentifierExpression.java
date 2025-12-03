package com.compiler.ast.expressions;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.errors.UnresolvedError;
import com.compiler.ast.Type;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public final class IdentifierExpression implements DeclarativeExpression {
    final String _c = "IdentifierExpression";
    Token value;

    public IdentifierExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.value();
    }

    public String name() {
        return this.value.value();
    }

    @Override
    public void validateType(Type type, BlockStatement parent) {
        var currentType = parent.getVar(this.value.value());
        if (currentType == null) {
            parent.addVar(this.value.value(), type);
            return;
        }
        if (type.token().kind() == TokenKind.OBJECT) {
            return;
        }
        if (currentType.token().value().equals(type.token().value())) {
            return;
        }
        throw new InvalidTypeError(type.token(), this.value);
    }

    @Override
    public Token getIdentifier() {
        return this.value;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isBoolean'");
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        var type = parent.getVar(this.value.value());
        if (type == null) {
            throw new UnresolvedError(this.value);
        }
        return TokenKind.isNumberType(type.token().kind());
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return parent.getVar(this.value.value()) != null;
    }
}
