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
        PrattRegistry.led(TokenKind.AND, BindingPower.LOGICAL, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.OR, BindingPower.LOGICAL, Parser::parseBinaryExpression);

        // relationals
        PrattRegistry.led(TokenKind.LESS, BindingPower.RELATIONAL, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.LESS_EQUALS, BindingPower.RELATIONAL, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.GREATER, BindingPower.RELATIONAL, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.GREATER_EQUALS, BindingPower.RELATIONAL, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.EQUALS, BindingPower.RELATIONAL, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.NOT_EQUALS, BindingPower.RELATIONAL, Parser::parseBinaryExpression);

        // additive
        PrattRegistry.led(TokenKind.PLUS, BindingPower.ADDITIVE, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.MINUS, BindingPower.ADDITIVE, Parser::parseBinaryExpression);

        // multiplicative
        PrattRegistry.led(TokenKind.STAR, BindingPower.MULTIPLICATIVE, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.SLASH, BindingPower.MULTIPLICATIVE, Parser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.PERCENT, BindingPower.MULTIPLICATIVE, Parser::parseBinaryExpression);

        // literals
        PrattRegistry.nud(TokenKind.NUMBER_EXPRESSION, BindingPower.PRIMARY, Parser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.STRING_EXPRESSION, BindingPower.PRIMARY, Parser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.TRUE, BindingPower.PRIMARY, Parser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.FALSE, BindingPower.PRIMARY, Parser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.CHAR, BindingPower.PRIMARY, Parser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.OPEN_PAREN, BindingPower.PRIMARY, Parser::parseParenthesizedExpression);

        // identifiers
        PrattRegistry.nud(TokenKind.IDENTIFIER, BindingPower.PRIMARY, Parser::parseIdentifierExpression);
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
