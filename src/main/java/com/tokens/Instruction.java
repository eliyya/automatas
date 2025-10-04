package com.tokens;

import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Instruction implements Node {

    private final Node lhs;
    private Node rhs;
    private Token eof;

    public Instruction(Node lhs, Token eof) {
        this.lhs = lhs;
        this.eof = eof;
    }

    public Instruction(Node lhs, Node rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
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
        hash.put("op", ";");
        hash.put("lhs", lhs.toHashMap());
        if (eof != null) {
            hash.put("rhs", eof.getType());
        } else {
            hash.put("rhs", rhs.toHashMap());
        }
        return hash;
    }

    @Override
    public String toString() {
        return this.lhs.toString() + "; " + (rhs != null ? eof.getType() : rhs.toString());
    }
}
