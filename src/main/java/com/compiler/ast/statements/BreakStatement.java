package com.compiler.ast.statements;

import com.compiler.ast.Statement;
import com.compiler.lexer.Token;

public class BreakStatement implements Statement {
    String _c = "BreakStatment";
    Token label;

    public BreakStatement(Token label) {
        this.label = label;
    }

    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'statment'");
    }
    
}
