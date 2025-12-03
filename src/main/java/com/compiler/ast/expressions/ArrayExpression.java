package com.compiler.ast.expressions;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.Type;
import com.compiler.lexer.Token;

public class ArrayExpression implements Expression {
    String _c = "ArrayExpression";
    List<Expression> elements;

    public ArrayExpression(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public void validateType(Type type, BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }

    @Override
    public Token getToken() {
        return elements.getFirst().getToken();
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        return false;
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isNumber'");
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        return this.elements.stream().allMatch(e -> e.isDeclared(parent));
    }
}
