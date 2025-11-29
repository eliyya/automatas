package com.compiler.ast.statments.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;

public class IfStatment implements ContolFlowStatment {
    String _c = "IfStatment";
    Expression condition;
    BlockStatment body;
    BlockStatment elseBody;

    public IfStatment(Expression condition, BlockStatment body) {
        this.condition = condition;
        this.body = body;
    }

    public IfStatment(Expression condition, BlockStatment body, BlockStatment elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    @Override
    public String toString() {
        if (elseBody == null) {
            return "if (" + condition + ") " + body;
        }
        return "if (" + condition + ") " + body + " else " + elseBody;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
