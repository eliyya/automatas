package com.compiler.parser;

import java.util.HashMap;
import java.util.Map;

import com.compiler.lexer.TokenKind;
import com.compiler.parser.handlers.LedHandler;
import com.compiler.parser.handlers.NudHandler;
import com.compiler.parser.handlers.StatmentHandler;

public class PrattRegistry {
    public static final Map<TokenKind, StatmentHandler> stmtLU = new HashMap<>();
    public static final Map<TokenKind, NudHandler> nudLU = new HashMap<>();
    public static final Map<TokenKind, LedHandler> ledLU = new HashMap<>();
    public static final Map<TokenKind, BindingPower> bpLU = new HashMap<>();
    static {
        // logical
        PrattRegistry.led(TokenKind.AND, BindingPower.LOGICAL, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.OR, BindingPower.LOGICAL, ExpressionParser::parseBinaryExpression);

        // relationals
        PrattRegistry.led(TokenKind.LESS, BindingPower.RELATIONAL, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.LESS_EQUALS, BindingPower.RELATIONAL, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.GREATER, BindingPower.RELATIONAL, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.GREATER_EQUALS, BindingPower.RELATIONAL, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.EQUALS, BindingPower.RELATIONAL, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.NOT_EQUALS, BindingPower.RELATIONAL, ExpressionParser::parseBinaryExpression);

        // additive
        PrattRegistry.led(TokenKind.PLUS, BindingPower.ADDITIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.MINUS, BindingPower.ADDITIVE, ExpressionParser::parseBinaryExpression);

        // multiplicative
        PrattRegistry.led(TokenKind.STAR, BindingPower.MULTIPLICATIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.SLASH, BindingPower.MULTIPLICATIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.PERCENT, BindingPower.MULTIPLICATIVE, ExpressionParser::parseBinaryExpression);

        // literals
        PrattRegistry.nud(TokenKind.NUMBER_EXPRESSION, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.STRING_EXPRESSION, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.TRUE, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.FALSE, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.CHAR, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.OPEN_PAREN, BindingPower.PRIMARY, ExpressionParser::parseParenthesizedExpression);

        // identifiers
        PrattRegistry.nud(TokenKind.IDENTIFIER, BindingPower.PRIMARY, ExpressionParser::parseIdentifierExpression);

        // declaration
        PrattRegistry.stmt(TokenKind.FLOAT, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.INT, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.BOOLEAN, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.DOUBLE, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.SHORT, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.LONG, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.BYTE, StatmentParser::parseTypedVarStatment);
        PrattRegistry.stmt(TokenKind.STRING, StatmentParser::parseTypedVarStatment);
    }

    public static void led(TokenKind kind, BindingPower bp, LedHandler fn) {
        bpLU.put(kind, bp);
        ledLU.put(kind, fn);
    }

    public static void nud(TokenKind kind, BindingPower bp, NudHandler fn) {
        bpLU.put(kind, BindingPower.PRIMARY);
        nudLU.put(kind, fn);
    }

    public static void stmt(TokenKind kind, StatmentHandler fn) {
        bpLU.put(kind, BindingPower.DEFAULT_BP);
        stmtLU.put(kind, fn);
    }
}
