package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;

public class ExpressionStatement implements Statement {
    final String _c = "ExpressionStatement";
    Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        return expression + ";";
    }
    
    @Override
    public void validate(BlockStatement parent) {
        // ExpressionStatement is not necessary to validate
    }
}
