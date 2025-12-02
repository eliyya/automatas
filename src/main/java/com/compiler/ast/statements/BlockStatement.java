package com.compiler.ast.statements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compiler.ast.Statement;
import com.compiler.lexer.Token;

public class BlockStatement implements Statement {
    final String _c = "BlockStatement";
    List<Statement> body;

    private HashMap<String, Token> vars = new HashMap<>();

    public Map<String, Token> getVars() {
        return vars;
    }

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
    
    public void validate() {
        for (var elem : body) {
            elem.validate(this);
        }
    }
    
    @Override
    public void validate(BlockStatement parent) {
        for (var elem : body) {
            elem.validate(parent);
        }
    }
}
