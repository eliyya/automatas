package com.compiler.nodes;

import java.util.HashMap;

import com.compiler.Token;
import com.google.gson.GsonBuilder;

public class Expression implements Node {

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
            return this.value.value();
        }
        return this.toString();
    }

    @Override
    public String toString() {
        if (this.operator == null) {
            return this.lhs.getValue();
        }
        return "(" + this.lhs.getValue() + " " + this.operator.value() + " " + this.rhs.getValue() + ")";
    }

    @Override
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> persona = new HashMap<>();
        if (this.operator == null) {
            persona.put("", this.value.value());
        } else {
            persona.put("op", this.operator.value());
            if (this.lhs.isExpression()) {
                persona.put("lhs", this.lhs.toHashMap());
            } else {
                persona.put("lhs", this.lhs.getValue());
            }
            if (this.rhs.isExpression()) {
                persona.put("rhs", this.rhs.toHashMap());
            } else {
                persona.put("rhs", this.rhs.getValue());
            }
        }
        return persona;
    }

    @Override
    public String toJSON() {
        return toJSON(1);
    }

    public String toJSON(int indent) {
        if (this.rhs == null) {
            return "\"" + this.value.value() + "\"";
        }
        var hash = new HashMap<String, Object>();
        hash.put("op", this.operator.value());
        if (this.lhs.isExpression()) {
            hash.put("lhs", this.lhs.toHashMap());
        } else {
            hash.put("lhs", this.lhs.getValue());
        }
        if (this.rhs.isExpression()) {
            hash.put("rhs", this.rhs.toHashMap());
        } else {
            hash.put("rhs", this.rhs.getValue());
        }

        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(hash);
    }
}
