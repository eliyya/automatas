package com.compiler.ast.expressions;

import java.util.List;
import java.util.stream.Collectors;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.Type;
import com.compiler.ast.types.ArrayType;
import com.compiler.lexer.Token;

public class ArrayExpression implements Expression {
    List<Expression> elements;

    public ArrayExpression(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public void validateType(Type type, BlockStatement parent) {
        System.out.println(type);
        System.out.println(this.elements);
        switch (type) {
            case ArrayType at -> {
                for (var element : this.elements) {
                    element.validateType(at.inner(), parent);
                }
            }
            default -> throw new AssertionError();
        }
    }

    @Override
    public Token getIdentifier() {
        return elements.getFirst().getIdentifier();
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

    @Override
    public Token token() {
        return this.elements.getFirst().token();
    }

    @Override
    public String toString() {
        return "new ArrayList<>(List.of("+this.elements.stream().map(e -> e.toString()).collect(Collectors.joining(", "))+"))";
    }
}
