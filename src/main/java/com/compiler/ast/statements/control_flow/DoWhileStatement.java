package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
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
        this.condition.validateType(BlockStatement.BooleanType, parent);
        this.body.validate(parent);
    }

    @Override
    public void validate(BlockStatement parent, Type returnType) {
        this.condition.validateType(BlockStatement.BooleanType, parent);
        this.body.validate(parent.getVars(), parent.getFuncs(), returnType);
    }
    
}
