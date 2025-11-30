package com.compiler.parser;

import java.util.HashMap;
import java.util.Map;

import com.compiler.lexer.TokenKind;
import com.compiler.parser.handlers.LedHandler;
import com.compiler.parser.handlers.NudHandler;
import com.compiler.parser.handlers.StatementHandler;

public class PrattRegistry {
    public static final Map<TokenKind, StatementHandler> stmtLU = new HashMap<>();
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
        PrattRegistry.stmt(TokenKind.FLOAT, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.INT, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.BOOLEAN, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.DOUBLE, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.SHORT, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.LONG, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.BYTE, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.STRING, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.CHAR, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.VOID, StatementParser::parseDeclaratioStatement);
        PrattRegistry.stmt(TokenKind.VAR, StatementParser::parseDeclaratioStatement);

        // identifier
        PrattRegistry.stmt(TokenKind.IDENTIFIER, StatementParser::parseIdentifierStatement);

        // unary
        PrattRegistry.stmt(TokenKind.PLUS_PLUS, StatementParser::parseUaryOperationStatement);
        PrattRegistry.stmt(TokenKind.MINUS_MINUS, StatementParser::parseUaryOperationStatement);

        // contol flow
        PrattRegistry.stmt(TokenKind.WHILE, StatementParser::parseControlFlowStatement);
        PrattRegistry.stmt(TokenKind.IF, StatementParser::parseControlFlowStatement);
        PrattRegistry.stmt(TokenKind.FOR, StatementParser::parseControlFlowStatement);
        PrattRegistry.stmt(TokenKind.DO, StatementParser::parseControlFlowStatement);
        PrattRegistry.stmt(TokenKind.OPEN_CURLY, StatementParser::parseBlockStatement);
        PrattRegistry.stmt(TokenKind.RETURN, StatementParser::parseReturnStatement);
        PrattRegistry.stmt(TokenKind.BREAK, StatementParser::parseBreakStatement);
    }

    public static void led(TokenKind kind, BindingPower bp, LedHandler fn) {
        bpLU.put(kind, bp);
        ledLU.put(kind, fn);
    }

    public static void nud(TokenKind kind, BindingPower bp, NudHandler fn) {
        bpLU.put(kind, BindingPower.PRIMARY);
        nudLU.put(kind, fn);
    }

    public static void stmt(TokenKind kind, StatementHandler fn) {
        bpLU.put(kind, BindingPower.DEFAULT_BP);
        stmtLU.put(kind, fn);
    }
}
