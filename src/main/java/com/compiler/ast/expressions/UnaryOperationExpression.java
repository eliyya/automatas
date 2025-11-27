package com.compiler.ast.expressions;

import com.compiler.ast.Expression;
import com.compiler.lexer.Token;

public class UnaryOperationExpression implements Expression {
    final String _c = "UnaryOperationExpression";
    Token identifier;
    Token operation;
    boolean prefix;

    public UnaryOperationExpression(Token identifier, Token operation, boolean prefix) {
        this.identifier = identifier;
        this.operation = operation;
        this.prefix = prefix;
    }

    @Override
    public void expression() {
        throw new UnsupportedOperationException("Unimplemented method 'expression'");
    }
}
