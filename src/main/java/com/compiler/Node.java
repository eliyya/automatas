package com.compiler;

import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Node {
    private final Token op;
    private final Node lhs;
    private final Node rhs;

    public Node(Token op, Node lhs, Node rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Node(Token op) {
        this.op = op;
        this.lhs = null;
        this.rhs = null;
    }

    public Token op() {
        return this.op;
    }

    public Node lhs() {
        return this.lhs;
    }

    public Node rhs() {
        return this.rhs;
    }
    
    public boolean isAtom() {
        return this.lhs == null && this.rhs == null;
    }

    public HashMap<String, Object> toHashMap() {
        var hash = new HashMap<String, Object>();
        hash.put("op", op.value());
        if (!isAtom()) {
            hash.put("lhs", lhs.isAtom() ? lhs.op().value() : lhs.toHashMap());
            hash.put("rhs", rhs.isAtom() ? rhs.op().value() : rhs.toHashMap());
        }
        return hash;
    }

    public String toJSON() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(toHashMap());
    }
}
