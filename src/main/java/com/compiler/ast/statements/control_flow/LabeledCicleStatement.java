package com.compiler.ast.statements.control_flow;

import com.compiler.ast.Type;
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
        this.cicle.validate(parent);
    }

    @Override
    public String toString() {
        return this.label + ": " + this.cicle;
    }

    @Override
    public void validate(BlockStatement parent, Type returnType) {
        this.cicle.validate(parent, returnType);
    }

    @Override
    public Token token() {
        return this.label;
    }
    
}
