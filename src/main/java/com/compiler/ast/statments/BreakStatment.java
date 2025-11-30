package com.compiler.ast.statments;

import com.compiler.ast.Statment;
import com.compiler.lexer.Token;

public class BreakStatment implements Statment {
    String _c = "BreakStatment";
    Token label;

    public BreakStatment(Token label) {
        this.label = label;
    }

    @Override
    public void statment() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
