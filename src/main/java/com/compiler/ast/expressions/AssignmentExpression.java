package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
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
        return identifier.value() + " " + operator.value() + " " + expression + ";";
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        var identifierType = parent.getVar(identifier.value());
        if (identifierType == null) {
            parent.addVar(identifier.value(), type);
            identifierType = type;
        }
        expression.validateType(type, parent);
    }
    
}
