package com.tokens;

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
}
