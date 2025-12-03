package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

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
    public void validateType(Type type, BlockStatement parent) {
       switch (this.operator.kind()) {
            case LESS, GREATER, LESS_EQUALS, GREATER_EQUALS -> {
                if (!this.left.isNumber(parent) || !this.right.isNumber(parent)) {
                    throw new InvalidTypeError(type.token(), this.operator);
                }
            }
            case EQUALS, NOT_EQUALS -> {
                if (type.token().kind() != TokenKind.BOOLEAN) {
                    throw new InvalidTypeError(type.token(), this.operator);
                }
            }
            default -> {
                left.validateType(type, parent);
                right.validateType(type, parent);
            }
        }
    }

    @Override
    public Token getIdentifier() {
        return this.operator;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        var kind = this.operator.kind();
        if (kind == TokenKind.EQUALS || kind == TokenKind.NOT_EQUALS) {
            return true;
        } else if (kind == TokenKind.LESS || kind == TokenKind.GREATER || kind == TokenKind.LESS_EQUALS
                || kind == TokenKind.GREATER_EQUALS) {
            return this.left.isNumber(parent) && this.right.isNumber(parent);
        }
        return false;
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isNumber'");
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return this.left.isDeclared(parent) && this.right.isDeclared(parent);
    }
}
