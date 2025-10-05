package com.compiler.nodes;

import java.util.HashMap;

import com.compiler.Token;
import com.google.gson.GsonBuilder;

public class Expression implements Node {

    private final Token operator;
    private final Node lhs;
    private final Node rhs;   

    public Expression(Token operator, Node lhs, Node rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Node getLhs() {
        return lhs;
    }

    public Token getOp() {
        return operator;
    }

    public Node getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return "(" 
        + this.lhs.toString() + " " 
        + this.operator.value() + " " 
        + this.rhs.toString() + ")";
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> persona = new HashMap<>();
        persona.put("op", this.operator.value());
        persona.put("lhs", this.lhs.toHashMap());
        persona.put("rhs", this.rhs.toHashMap());
        return persona;
    }

    @Override
    public String toJSON() {
        return toJSON(1);
    }

    public String toJSON(int indent) {
        var hash = new HashMap<String, Object>();
        hash.put("op", this.operator.value());
        hash.put("lhs", this.lhs.toHashMap());
        hash.put("rhs", this.rhs.toHashMap());

        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(hash);
    }
}
