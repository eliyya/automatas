package com.compiler.ast.statements.control_flow;

import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.lexer.Token;

public class LabeledCicleStatement implements ContolFlowStatement {
    String _c = "LabeledCicleStatment";
    Token label;
    ContolFlowStatement cicle;

    public LabeledCicleStatement(Token label, ContolFlowStatement cicle) {
        this.label = label;
        this.cicle = cicle;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
