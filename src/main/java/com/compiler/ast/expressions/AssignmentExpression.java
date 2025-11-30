package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
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
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
    
}
