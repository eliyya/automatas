package com.compiler.nodes;

import java.util.HashMap;

import com.compiler.Token;
import com.google.gson.GsonBuilder;

public class Declaration implements Node {

    private final Token type;
    private final Expression value;

    public Declaration(Token type, Expression value) {
        this.type = type;
        this.value = value;
    }

    public Token getType() {
        return type;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type.value() + " " + value.toString();
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
        if (value.isExpression()) {
            hash.put("rhs", value.toHashMap());
        } else {
            hash.put("rhs", value.getValue());
        }
        return hash;
    }
}
