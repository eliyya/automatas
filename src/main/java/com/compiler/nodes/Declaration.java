package com.compiler.nodes;

import java.util.HashMap;

import com.compiler.Token;
import com.google.gson.GsonBuilder;

public class Declaration implements Node {

    private final Token type;
    private final Node expression;

    public Declaration(Token type, Node expression) {
        this.type = type;
        this.expression = expression;
    }

    public Token getType() {
        return type;
    }

    public Node getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return type.value() + " " + expression.toString();
    }

    @Override
    public String toJSON() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(toHashMap());
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        var hash = new HashMap<String, Object>();
        hash.put("op", "declaration");
        hash.put("lhs", type.value());
        if (expression instanceof Expression exp) {
            hash.put("rhs", exp.toHashMap());            
        } if (expression instanceof Atom atom) {
            hash.put("rhs", atom.getToken().value());
        }
        return hash;
    }
}
