package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public class BinaryExpression implements Expression {
    final String _c = "BinaryExpression";
    Expression left;
    Token operator;
    Expression right;

    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator.value() + " " + right.toString();
    }
    
    @Override
    public void validateType(Token type, BlockStatement parent) {
        left.validateType(type, parent);
        right.validateType(type, parent);
    }
}
