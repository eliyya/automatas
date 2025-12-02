package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;

public class WhileStatement implements ContolFlowStatement {
    String _c = "WhileStatement";
    Expression condition;
    BlockStatement body;

    public WhileStatement(Expression condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "while (" + condition + ") " + body;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
