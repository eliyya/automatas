package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public class ArrayAccessExpression implements Expression {
    private final Expression array;
    private final Expression index;

    public ArrayAccessExpression(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public Token token() {
        return array.token();
    }

    @Override
    public String toString() {
        return array + ".get(" + index + ")";
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return true;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        return false;
    }

    @Override
    public void validateType(Type type, BlockStatement parent) {
        // TODO: Implement type validation for array access
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        return true; // Assuming array access returns a number
    }

    @Override
    public Token getIdentifier() {
        return array.token();
    }
}
