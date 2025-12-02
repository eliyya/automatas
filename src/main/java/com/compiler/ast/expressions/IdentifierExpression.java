package com.compiler.ast.expressions;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

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

    @Override
    public void validateType(Token type, BlockStatement parent) {
        var currentType = parent.getVars().get(this.value.value());
        if (currentType == null) {
            parent.getVars().put(this.value.value(), type);
            return;
        }
        if (currentType.value().equals(type.value())) {
            return;
        }
        throw new UnsupportedOperationException("Type mismatch: " + currentType.value() + " != " + type.value());
    }
}
