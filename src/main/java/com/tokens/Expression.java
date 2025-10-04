package com.tokens;

import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class Expression {

    private Token operator;
    private Expression lhs;
    private Expression rhs;
    private Token value;

    public Expression(Token lhs) {
        this.value = lhs;
    }

    public Expression(Token operator, Expression lhs, Expression rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Expression getLhs() {
        return lhs;
    }

    public Token getOperator() {
        return operator;
    }

    public Expression getRhs() {
        return rhs;
    }

    public boolean isExpression() {
        return this.operator != null;
    }

    public String getValue() {
        if (this.operator == null) {
            return this.value.getValue();
        }
        return this.toString();
    }

    @Override
    public String toString() {
        if (this.operator == null) {
            return this.lhs.getValue();
        }
        return "(" + this.lhs.getValue() + " " + this.operator.getValue() + " " + this.rhs.getValue() + ")";
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> persona = new HashMap<>();
        if (this.operator == null) {
            persona.put("value", this.value.getValue());
        } else {
            persona.put("operator", this.operator.getValue());
            persona.put("lhs", this.lhs.toHashMap());
            persona.put("rhs", this.rhs.toHashMap());
        }
        return persona;
    }

    public String toJSON() {
        return toJSON(1);
    }

    public String toJSON(int indent) {
        if (this.rhs == null) {
            return "\"" + this.value.getValue() + "\"";
        }
        var hash = new HashMap<String, Object>();
        hash.put("op", this.operator.getValue());
        hash.put("lhs", this.lhs.toHashMap());
        hash.put("rhs", this.rhs.toHashMap());
        
        return new GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(hash);
    }
}
