package com.compiler.ast.expressions;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public class FunctionCallExpression implements Expression {
    String _c = "FunctionCallExpression";
    Token name;
    List<Expression> parameters;

    public FunctionCallExpression(Token name, List<Expression> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return name.value() + "(" + parameters + ")";
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }

    @Override
    public Token getToken() {
        return this.name;
    }

    @Override
    public boolean isBoolean(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isBoolean'");
    }

    @Override
    public boolean isNumber(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isNumber'");
    }

    @Override
    public boolean isDeclared(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'isDeclared'");
    }
    
}
