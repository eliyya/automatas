package com.compiler.nodes;

import java.util.HashMap;

import com.compiler.Token;
import com.google.gson.GsonBuilder;

public class Atom implements Node {
    private final Token token;

    public Atom(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    @Override
    public String toString() {
        return this.token.value();
    }

    @Override
    public String toJSON() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(toHashMap());
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        var hash = new HashMap<String, Object>();
        hash.put("op", token.value());
        return hash;
    }
}
