package com.tokens;

import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Declaration {

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

    public String toJSON() {
        var hash = new HashMap<String, Object>();
        hash.put("type", type.getValue());
        hash.put("value", value.getValue());

        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(hash);
    }
}
