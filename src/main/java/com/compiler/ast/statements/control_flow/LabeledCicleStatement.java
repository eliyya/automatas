package com.compiler.ast.statements.control_flow;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.ContolFlowStatement;
import com.compiler.lexer.Token;

public class LabeledCicleStatement implements ContolFlowStatement {
    String _c = "LabeledCicleStatement";
    Token label;
    ContolFlowStatement cicle;

    public LabeledCicleStatement(Token label, ContolFlowStatement cicle) {
        this.label = label;
        this.cicle = cicle;
    }

    @Override
    public void validate(BlockStatement parent) {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }

    @Override
    public String toString() {
        return this.label + ": " + this.cicle;
    }
    
}
