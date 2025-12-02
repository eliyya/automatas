package com.compiler.ast.expressions;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public class ArrayExpression implements Expression {
    String _c = "ArrayExpression";
    List<Expression> elements;

    public ArrayExpression(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
