package com.compiler.parser;

import java.util.ArrayList;

import com.compiler.ast.Expression;
import com.compiler.ast.expressions.BinaryExpression;
import com.compiler.ast.expressions.FunctionExpression;
import com.compiler.ast.expressions.IdentifierExpression;
import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.ast.expressions.UnaryOperationExpression;
import com.compiler.ast.expressions.primary.BooleanExpression;
import com.compiler.ast.expressions.primary.CharExpression;
import com.compiler.ast.expressions.primary.NumberExpression;
import com.compiler.ast.expressions.primary.StringExpression;
import com.compiler.errors.ExpectedError;
import com.compiler.lexer.TokenKind;

public class ExpressionParser {

    public static Expression parseExpression(Parser parser, BindingPower bp) {
        var tokenKind = parser.currentTokenKind();
        var nud = PrattRegistry.nudLU.get(tokenKind);
        if (nud == null) {
            throw new RuntimeException("nud handler expected for token : " + tokenKind);
        }
        var left = nud.handle(parser);

        while (true) {
            var opkb = parser.currentTokenKind();
            if (opkb == TokenKind.SEMI || opkb == TokenKind.EOF) {
                break;
            }

            var opbp = PrattRegistry.bpLU.get(opkb);
            if (opbp == null || opbp.ordinal() <= bp.ordinal()) {
                break;
            }

            var led = PrattRegistry.ledLU.get(opkb);
            if (led == null) {
                if (opkb == TokenKind.OPEN_PAREN) {
                    throw new ExpectedError(parser, "operator", parser.currentToken());
                }
                throw new RuntimeException("led handler expected for token : " + opkb);
            }
            left = led.handle(parser, left, opbp);
        }
        return left;
    }

    public static Expression parseExpression(Parser parser) {
        return parseExpression(parser, BindingPower.DEFAULT_BP);
    }
    
    public static BinaryExpression parseBinaryExpression(Parser parser, Expression left, BindingPower bp) {
        var operator = parser.advance();
        var right = parseExpression(parser, bp);
        return new BinaryExpression(left, operator, right);
    }

    public static FunctionExpression parseFunctionExpression(Parser parser) throws ExpectedError {
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.OPEN_PAREN);
        var parameters = new ArrayList<Expression>();
        while (parser.currentTokenKind() != TokenKind.CLOSE_PAREN) {
            parameters.add(parseExpression(parser));
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                parser.advance();
                continue;
            }
            break;
        }
        parser.expect(TokenKind.CLOSE_PAREN);
        return new FunctionExpression(identifier, parameters);
    }

    public static Expression parseIdentifierExpression(Parser parser) {
        var identifier = parser.currentToken();
        var pp = parser.nextToken();
        switch (pp.kind()) {
            case PLUS_PLUS, MINUS_MINUS -> {
                parser.advance();
                parser.advance();
                return new UnaryOperationExpression(identifier, pp, false);
            }
            case OPEN_PAREN -> {
                return parseFunctionExpression(parser);
            }
            default -> {
                parser.advance();
                return new IdentifierExpression(identifier);
            }
        }
    }

    public static Expression parseParenthesizedExpression(Parser parser) {
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        return expression;
    }

    public static PrimaryExpression parsePrimaryExpression(Parser parser) {
        switch (parser.currentTokenKind()) {
            case NUMBER_EXPRESSION -> {
                var number = parser.advance();
                return new NumberExpression(number);
            }
            case STRING_EXPRESSION -> {
                var string = parser.advance();
                return new StringExpression(string);
            }
            case CHAR -> {
                var string = parser.advance();
                return new CharExpression(string);
            }
            case TRUE, FALSE -> {
                var bool = parser.advance();
                return new BooleanExpression(bool);
            }
            default ->
                throw new RuntimeException("Cannot parse primary expression from token : " + parser.currentTokenKind());
        }
    }
}
