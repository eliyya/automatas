package com.compiler.ast.statments.control_flow;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.lexer.Token;

public class ForStatment implements ContolFlowStatment {
    String _c = "ForStatment";
    List<DeclarationStatment> statment;
    Token collection;
    Expression condition;
    Expression increment;
    BlockStatment body;
    
    public ForStatment(List<DeclarationStatment> statment, Token collection, BlockStatment body) {
        this.statment = statment;
        this.collection = collection;
        this.body = body;
    }

    public ForStatment(List<DeclarationStatment> statment, Expression condition,Expression increment, BlockStatment body) {
        this.statment = statment;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public void statment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
