package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;

public class DoWhileStatement implements ContolFlowStatement {
    String _c = "DoWhileStatement";
    BlockStatement body;
    Expression condition;

    public DoWhileStatement(BlockStatement body, Expression condition) {
        this.body = body;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "do " + body + " while (" + condition + ");";
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
