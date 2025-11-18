package com.compiler.parser;

import java.util.HashMap;
import java.util.Map;

import com.compiler.ast.Expression;
import com.compiler.ast.expressions.NumberExpression;
import com.compiler.ast.expressions.StringExpression;
import com.compiler.ast.expressions.BinaryExpression;
import com.compiler.ast.expressions.IdentifierExpression;
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
        PrattRegistry.led(TokenKind.AND, BindingPower.LOGICAL, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.OR, BindingPower.LOGICAL, PrattRegistry::parseBinaryExpression);

        // relationals
        PrattRegistry.led(TokenKind.LESS, BindingPower.RELATIONAL, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.LESS_EQUALS, BindingPower.RELATIONAL, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.GREATER, BindingPower.RELATIONAL, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.GREATER_EQUALS, BindingPower.RELATIONAL, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.EQUALS, BindingPower.RELATIONAL, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.NOT_EQUALS, BindingPower.RELATIONAL, PrattRegistry::parseBinaryExpression);

        // additive
        PrattRegistry.led(TokenKind.PLUS, BindingPower.ADDITIVE, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.MINUS, BindingPower.ADDITIVE, PrattRegistry::parseBinaryExpression);

        // multiplicative
        PrattRegistry.led(TokenKind.STAR, BindingPower.MULTIPLICATIVE, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.SLASH, BindingPower.MULTIPLICATIVE, PrattRegistry::parseBinaryExpression);
        PrattRegistry.led(TokenKind.PERCENT, BindingPower.MULTIPLICATIVE, PrattRegistry::parseBinaryExpression); 

        // literals
        PrattRegistry.nud(TokenKind.NUMBER, BindingPower.PRIMARY, PrattRegistry::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.STRING, BindingPower.PRIMARY, PrattRegistry::parsePrimaryExpression);
        PrattRegistry.nud(TokenKind.IDENTIFIER, BindingPower.PRIMARY, PrattRegistry::parsePrimaryExpression);
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

    public static Expression parsePrimaryExpression(Parser parser) {
        switch (parser.currentTokenKind()) {
            case NUMBER:
                var number = Float.parseFloat(parser.advance().value());
                return new NumberExpression(number);
            case STRING:
                var string = parser.advance().value();
                return new StringExpression(string);
            case IDENTIFIER:
                var identifier = parser.advance().value();
                return new IdentifierExpression(identifier);
            default:
                throw new RuntimeException("Cannot parse primary expression from token : " + parser.currentTokenKind());
        }
    }
    
    public static Expression parseBinaryExpression(Parser parser, Expression left, BindingPower bp) {
        var operator = parser.advance();
        var right = PrattRegistry.parseExpression(parser, bp);
        System.out.println("BinaryExpression : " + left + " " + operator.value() + " " + right);
        return new BinaryExpression(left, operator, right);
    }

    public static Expression parseExpression(Parser parser, BindingPower bp) {
    var tokenKind = parser.currentTokenKind();
    var nud = nudLU.get(tokenKind);
    if (nud == null) {
        throw new RuntimeException("nud handler expected for token : " + tokenKind);
    }
    var left = nud.handle(parser);
    
    while (true) {
        var opkb = parser.currentTokenKind();
        // Stop if we've reached the end of the expression
        if (opkb == TokenKind.SEMI || opkb == TokenKind.EOF) {
            break;
        }
        
        var opbp = bpLU.get(opkb);
        // Stop if the next operator doesn't have higher precedence
        if (opbp == null || opbp.ordinal() <= bp.ordinal()) {
            break;
        }
        
        var led = ledLU.get(opkb);
        if (led == null) {
            throw new RuntimeException("led handler expected for token : " + opkb);
        }
        left = led.handle(parser, left, opbp);  // Use opbp instead of bp for the right binding power
    }
    return left;
}
}
