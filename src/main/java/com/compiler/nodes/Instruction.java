package com.compiler.nodes;

import java.util.HashMap;

import com.compiler.Token;
import com.google.gson.GsonBuilder;

public class Instruction implements Node {

    /**
     * Left hand side
     * could be a declaration or an expression
     */
    private final Node lhs;
    /**
     * Right hand side
     * could be a Declaration, Atom or an Expression
     */
    private final Node rhs;
    private final Token op;

    public Instruction(Token op, Node lhs, Node rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * Left hand side
     * could be a declaration or an expression
     */
    public Node getLhs() {
        return lhs;
    }

    /**
     * Right hand side
     * could be a Declaration, Atom or an Expression
     */
    public Node getRhs() {
        return rhs;
    }

    public Token getOp() {
        return op;
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
        hash.put("rhs", rhs.toHashMap());
        return hash;
    }

    @Override
    public String toString() {
        return this.lhs.toString() + "; " + rhs.toString();
    }
}
