package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public final class AssignmentExpression implements DeclarativeExpression {
    final String _c = "AssignmentExpression";
    Token identifier;
    Token operator;
    Expression expression;

    public AssignmentExpression(Token identifier, Token operator, Expression expression) {
        this.identifier = identifier;
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return identifier.value() + " " + operator.value() + " " + expression;
    }

    @Override
    public void validateType(Type type, BlockStatement parent) {
        var identifierType = parent.getVar(identifier.value());
        if (identifierType == null) {
            parent.addVar(identifier.value(), type);
            identifierType = type;
        }
        expression.validateType(type, parent);
    }

    @Override
    public Token getToken() {
        return this.identifier;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        return this.expression.isBoolean(parent);
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isNumber'");
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return parent.getVar(this.identifier.value()) != null;
    }
    
}
