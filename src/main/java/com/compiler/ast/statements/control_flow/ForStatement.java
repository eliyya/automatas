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
    String _c = "ForStatment";
    ForType type;
    DeclarationVariableStatement statment;
    Token collection;
    Expression condition;
    Expression increment;
    BlockStatement body;
    
    public ForStatement(DeclarationVariableStatement statment, Token collection, BlockStatement body) {
        this.statment = statment;
        this.collection = collection;
        this.body = body;
        this.type = ForType.FOREACH;
    }

    public ForStatement(DeclarationVariableStatement statment, Expression condition, Expression increment, BlockStatement body) {
        this.statment = statment;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
        this.type = ForType.FORI;
    }

    @Override
    public String toString() {
        if (type == ForType.FOREACH) {
            return "for (" + statment + " : " + collection.value() + ") " + body;
        }
        return "for (" + statment + "; " + condition + "; " + increment + ") " + body;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
