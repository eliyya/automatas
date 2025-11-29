package com.compiler.ast.statments.control_flow;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.lexer.Token;

enum ForType {
    FORI,
    FOREACH
}

public class ForStatment implements ContolFlowStatment {
    String _c = "ForStatment";
    ForType type;
    DeclarationStatment statment;
    Token collection;
    Expression condition;
    Expression increment;
    BlockStatment body;
    
    public ForStatment(DeclarationStatment statment, Token collection, BlockStatment body) {
        this.statment = statment;
        this.collection = collection;
        this.body = body;
        this.type = ForType.FOREACH;
    }

    public ForStatment(DeclarationStatment statment, Expression condition, Expression increment, BlockStatment body) {
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
    public void statment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
