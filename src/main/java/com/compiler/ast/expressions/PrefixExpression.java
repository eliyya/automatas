package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.Type;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

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
    public void validateType(Type type, BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }

    @Override
    public Token getIdentifier() {
        if (this.suffix) return this.expression.getIdentifier();
        return this.operator;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isBoolean'");
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        if (this.operator.kind() == TokenKind.NOT) {
            return false;
        }
        return this.expression.isNumber(parent);
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return this.expression.isDeclared(parent);
    }

    @Override
    public Token token() {
        if (this.suffix) return this.expression.token();
        return this.operator;
    }
}
