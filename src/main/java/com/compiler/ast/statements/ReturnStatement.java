package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;

public class ReturnStatement implements Statement {
    String _c = "ReturnStatement";
    Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }
    
}
