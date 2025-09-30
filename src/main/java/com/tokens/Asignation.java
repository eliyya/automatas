package com.tokens;

public class Asignation {
    private Token operator;
    private Token name;
    private Expression value;

    public Asignation(Token operator, Token name, Expression value) {
        this.operator = operator;
        this.name = name;
        this.value = value;
    }

    public Token getOperator() {
        return operator;
    }

    public Token getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name.getValue() + " " + operator.getValue() + " " + value.toString();
    }

    public String toJSON() {
        return "{\"name\": \"" + name.getValue() + "\", \"operator\": \"" + operator.getValue() + "\", \"value\": " + value.toJSON() + "}";
    }
}
