package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.ast.statements.declaration.DeclarationVariableStatement;
import com.compiler.lexer.Token;

enum ForType {
    FORI,
    FOREACH
}

public class ForStatement implements ContolFlowStatement {
    String _c = "ForStatement";
    ForType type;
    DeclarationVariableStatement Statement;
    Expression collection;
    Expression condition;
    Expression increment;
    BlockStatement body;
    
    public ForStatement(DeclarationVariableStatement Statement, Expression collection, BlockStatement body) {
        this.Statement = Statement;
        this.collection = collection;
        this.body = body;
        this.type = ForType.FOREACH;
    }

    public ForStatement(DeclarationVariableStatement Statement, Expression condition, Expression increment, BlockStatement body) {
        this.Statement = Statement;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
        this.type = ForType.FORI;
    }

    @Override
    public String toString() {
        if (type == ForType.FOREACH) {
            return "for (" + Statement + " : " + collection.toString() + ") " + body;
        }
        return "for (" + Statement + "; " + condition + "; " + increment + ") " + body;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }

    @Override
    public void validate(BlockStatement parent, Token returnType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }
    
}
