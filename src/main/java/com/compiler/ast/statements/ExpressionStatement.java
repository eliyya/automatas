package com.compiler.ast.statements;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;
import com.compiler.ast.expressions.FunctionCallExpression;
import com.compiler.errors.UnresolvedError;

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
        if (this.expression instanceof FunctionCallExpression fc) {
            if (!fc.isDeclared(parent)) {
                throw new UnresolvedError(this.expression.getToken());
            }
        } else if (!this.expression.isDeclared(parent)) {
            throw new UnresolvedError(this.expression.getToken());
        }
    }
}
