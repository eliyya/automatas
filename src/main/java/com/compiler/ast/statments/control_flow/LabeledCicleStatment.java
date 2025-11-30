package com.compiler.ast.statments.control_flow;

import com.compiler.ast.statments.ContolFlowStatment;
import com.compiler.lexer.Token;

public class LabeledCicleStatment implements ContolFlowStatment {
    String _c = "LabeledCicleStatment";
    Token label;
    ContolFlowStatment cicle;

    public LabeledCicleStatment(Token label, ContolFlowStatment cicle) {
        this.label = label;
        this.cicle = cicle;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
