package com.tokens;

import java.util.HashMap;

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
        return type.getValue() + " " + value.toString();
    }

    @Override
    public String toJSON() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(toHashMap());
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        var hash = new HashMap<String, Object>();
        hash.put("op", "declaration");
        hash.put("lhs", type.getValue());
        hash.put("rhs", value.toHashMap());
        return hash;
    }
}
