package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.lexer.Token;

public class PrefixExpression implements Expression {
    final String _c = "PrefixExpression";
    Expression expression;
    Token operator;
    boolean suffix;

    public PrefixExpression(Token operation, Expression expression, boolean suffix) {
        this.expression = expression;
        this.operator = operation;
        this.suffix = suffix;
    }    

    public PrefixExpression(Token operation, Expression expression) {
        this.expression = expression;
        this.operator = operation;
    }

    @Override
    public String toString() {
        if (suffix) return expression.toString() + operator.value();
        else return operator.value() + expression.toString();
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
