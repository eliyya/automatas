package com.compiler.ast.statements;

import java.util.List;

import com.compiler.ast.Statement;

public class BlockStatement implements Statement {
    final String _c = "BlockStatmennt";
    List<Statement> body;

    public BlockStatement(List<Statement> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        var text = "{\n";
        for (var elem : body) {
            text += elem + "\n";
        }
        text += "}";
        return text;
    }
    
    @Override
    public void statement() {
        throw new UnsupportedOperationException("Unimplemented method 'Statement'");
    }
}
