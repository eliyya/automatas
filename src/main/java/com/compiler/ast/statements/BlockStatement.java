package com.compiler.ast.statements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compiler.ast.Statement;
import com.compiler.lexer.Token;
import com.compiler.utils.JsonIgnore;

public class BlockStatement implements Statement {
    final String _c = "BlockStatement";
    List<Statement> body;
    @JsonIgnore
    private Map<String, Token> vars = new HashMap<>();

    public Token getVar(String identifier) {
        return vars.get(identifier);
    }

    public void addVar(String identifier, Token token) {
        vars.put(identifier, token);
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
