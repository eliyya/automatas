package com.compiler.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.compiler.ast.Expression;
import com.compiler.ast.Statement;
import com.compiler.lexer.TokenKind;

@FunctionalInterface
interface LedHandler {
    Expression handle(Parser parser, Expression left, BindingPower bp);
}

public class PrattRegistry {
    // statement
    public static final Map<TokenKind, Function<Parser, Statement>> stmtLU = new HashMap<>();
    // null denotation
    public static final Map<TokenKind, Function<Parser, Expression>> nudLU = new HashMap<>();
    // left denotation
    public static final Map<TokenKind, LedHandler> ledLU = new HashMap<>();
    // binding power
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

        // math
        PrattRegistry.led(TokenKind.PLUS, BindingPower.ADDITIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.MINUS, BindingPower.ADDITIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.STAR, BindingPower.MULTIPLICATIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.SLASH, BindingPower.MULTIPLICATIVE, ExpressionParser::parseBinaryExpression);
        PrattRegistry.led(TokenKind.PERCENT, BindingPower.MULTIPLICATIVE, ExpressionParser::parseBinaryExpression);

        // literals
        PrattRegistry.nud(TokenKind.NUMBER_EXPRESSION, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.STRING_EXPRESSION, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.TRUE, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.FALSE, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.CHAR_EXPRESSION, BindingPower.PRIMARY, ExpressionParser::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.OPEN_PAREN, BindingPower.PRIMARY, ExpressionParser::parseParenthesizedExpression);
        PrattRegistry.nud(TokenKind.OPEN_CURLY, BindingPower.PRIMARY, ExpressionParser::parseArrayExpression);
        PrattRegistry.nud(TokenKind.NOT, BindingPower.PRIMARY, ExpressionParser::parsePrefixExpression);
        PrattRegistry.nud(TokenKind.MINUS, BindingPower.PRIMARY, ExpressionParser::parsePrefixExpression);
        PrattRegistry.nud(TokenKind.PLUS_PLUS, BindingPower.PRIMARY, ExpressionParser::parsePrefixExpression);
        PrattRegistry.nud(TokenKind.MINUS_MINUS, BindingPower.PRIMARY, ExpressionParser::parsePrefixExpression);

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

    public static void nud(TokenKind kind, BindingPower bp, Function<Parser, Expression> fn) {
        bpLU.put(kind, BindingPower.PRIMARY);
        nudLU.put(kind, fn);
    }

    public static void stmt(TokenKind kind, Function<Parser, Statement> fn) {
        bpLU.put(kind, BindingPower.DEFAULT_BP);
        stmtLU.put(kind, fn);
    }
}
