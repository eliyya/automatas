package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.Type;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.lexer.Token;

public class DoWhileStatement implements ContolFlowStatement {
    String _c = "DoWhileStatement";
    BlockStatement body;
    Expression condition;
    Token doToken;

    public DoWhileStatement(BlockStatement body, Expression condition, Token doToken) {
        this.body = body;
        this.condition = condition;
        this.doToken = doToken;
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

    @Override
    public Token token() {
        return this.doToken;
    }
}
