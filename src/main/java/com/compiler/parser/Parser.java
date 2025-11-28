package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;
import com.compiler.ast.expressions.BinaryExpression;
import com.compiler.ast.expressions.BooleanExpression;
import com.compiler.ast.expressions.CharExpression;
import com.compiler.ast.expressions.IdentifierExpression;
import com.compiler.ast.expressions.NumberExpression;
import com.compiler.ast.expressions.StringExpression;
import com.compiler.ast.expressions.UnaryOperationExpression;
import com.compiler.ast.statments.AssignmentStatment;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
import com.compiler.ast.statments.ContolFlowStatment;
import com.compiler.ast.statments.control_flow.DoStatment;
import com.compiler.ast.statments.control_flow.ForStatment;
import com.compiler.ast.statments.control_flow.IfStatment;
import com.compiler.ast.statments.control_flow.WhileStatment;
import com.compiler.errors.ExpectedError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class Parser {
    public List<String> lines;
    List<Token> tokens;
    int position;

    private Parser(List<Token> tokens, List<String> lines) {
        this.tokens = tokens;
        this.lines = lines;
        this.position = 0;
    }

    public static BlockStatment parse(List<Token> tokens, List<String> lines) {
        var parser = new Parser(tokens, lines);
        return parse(false, parser);
    }

    public static BlockStatment parse(Parser parser) {
        return parse(true, parser);
    }

    private static BlockStatment parse(boolean inner, Parser parser) throws ExpectedError {
        var body = new ArrayList<Statment>();

        while (parser.hasTokens()) {
            if (inner && parser.currentTokenKind() == TokenKind.CLOSE_CURLY)
                break;
            var currentToken = parser.currentToken();
            // start with type
            if (TokenKind.isPrimitiveType(currentToken)) {
                for (var statment : parseVariableStatment(parser)) {
                    body.add(statment);
                }
                // start with identifier
            } else if (currentToken.kind() == TokenKind.IDENTIFIER) {
                var nextToken = parser.nextToken();
                if (TokenKind.isAssignment(nextToken.kind())) {
                    // continue with assignment
                    body.add(parseAssignment(parser));
                } else if (TokenKind.isUnaryOperation(nextToken.kind())) {
                    // continue with unary operation
                    body.add(parseUnaryOperation(parser));
                } else if (nextToken.kind() == TokenKind.OPEN_PAREN) {
                    // continue with function call
                    // TODO: implementar
                    // body.add(parseFunctionStatment(parser));
                } else {
                    var expression = parseExpression(parser);
                    var semi = parser.advance();
                    if (semi.kind() != TokenKind.SEMI) {
                        throw new ExpectedError(parser, ";", semi);
                    }
                    body.add(new ExpressionStatment(expression));
                }
                // start with unary operation
            } else if (TokenKind.isUnaryOperation(currentToken.kind())) {
                body.add(parseUnaryOperation(parser));
                // start with control flow
            } else if (TokenKind.isControlFlow(currentToken.kind())) {
                body.add(parseControlFlowStatment(parser));
            } else {
                // var expression = parseExpression(parser,
                // BindingPower.DEFAULT_BP);
                // var semi = parser.advance();
                // if (semi.kind() != TokenKind.SEMI) {
                // throw new ExpectedError(parser, ";", semi);
                // }
                // body.add(new ExpressionStatment(expression));
                throw new ExpectedError(parser, "statment", currentToken);
            }
        }

        return new BlockStatment(body);
    }

    public Token currentToken() {
        return tokens.get(position);
    }

    public Token getToken(int n) {
        return tokens.get(position + n);
    }

    private Token nextToken() {
        return tokens.get(position + 1);
    }

    Token advance() {
        var token = this.currentToken();
        this.position++;
        return token;
    }

    public TokenKind currentTokenKind() {
        return this.currentToken().kind();
    }

    private boolean hasTokens() {
        return this.position < this.tokens.size() && this.currentTokenKind() != TokenKind.EOF;
    }

    private Token expect(TokenKind kind, String expect) {
        var token = this.advance();
        if (token.kind() != kind) {
            throw new ExpectedError(this, expect, token);
        }
        return token;
    }

    private Token expect(TokenKind kind) {
        return this.expect(kind, kind.text());
    }

    public static Expression parsePrimaryExpression(Parser parser) {
        switch (parser.currentTokenKind()) {
            case NUMBER_EXPRESSION -> {
                var number = Float.parseFloat(parser.advance().value());
                return new NumberExpression(number);
            }
            case STRING_EXPRESSION -> {
                var string = parser.advance().value();
                return new StringExpression(string);
            }
            case CHAR -> {
                var string = parser.advance().value();
                return new CharExpression(string);
            }
            case TRUE, FALSE -> {
                var bool = parser.advance().value();
                return new BooleanExpression(bool);
            }
            case IDENTIFIER -> {
                var identifier = parser.advance();
                var pp = parser.currentToken();
                if (pp.kind() == TokenKind.PLUS_PLUS) {
                    parser.advance();
                    return new UnaryOperationExpression(identifier, pp, false);
                } else {
                    return new IdentifierExpression(identifier.value());
                }
            }
            default -> throw new RuntimeException("Cannot parse primary expression from token : " + parser.currentTokenKind());
        }
    }

    public static Expression parseBinaryExpression(Parser parser, Expression left, BindingPower bp) {
        var operator = parser.advance();
        var right = parseExpression(parser, bp);
        return new BinaryExpression(left, operator, right);
    }

    public static Expression parseExpression(Parser parser) {
        return parseExpression(parser, BindingPower.DEFAULT_BP);
    }

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
                throw new RuntimeException("led handler expected for token : " + opkb);
            }
            left = led.handle(parser, left, opbp);
        }
        return left;
    }

    // --------------
    // parse statment
    // --------------
    private static List<DeclarationStatment> parseVariableStatment(Parser parser) throws ExpectedError {
        var declarations = new ArrayList<DeclarationStatment>();
        var tokenKind = parser.advance();
        if (!TokenKind.isPrimitiveType(tokenKind)) {
            throw new ExpectedError(parser, "primitive type", tokenKind);
        }
        while (true) {
            var identifier = parser.expect(TokenKind.IDENTIFIER);
            var equal = parser.advance();
            if (equal.kind() == TokenKind.SEMI) {
                declarations.add(new DeclarationStatment(tokenKind, identifier, null));
                return declarations;
            }
            if (!(equal.kind() == TokenKind.ASSIGNMENT || equal.kind() == TokenKind.COMMA)) {
                throw new ExpectedError(parser, ";", equal);
            }
            if (equal.kind() == TokenKind.COMMA) {
                declarations.add(new DeclarationStatment(tokenKind, identifier, null));
                continue;
            }
            var expression = parseExpression(parser);
            declarations.add(new DeclarationStatment(tokenKind, identifier, expression));
            var semi = parser.advance();
            if (semi.kind() == TokenKind.COMMA) {
                continue;
            }
            if (semi.kind() != TokenKind.SEMI) {
                throw new ExpectedError(parser, ";", semi);
            }
            return declarations;
        }
    }

    private static DeclarationStatment parseUniqueVariableStatment(Parser parser) throws ExpectedError {
        var tokenKind = parser.advance();
        if (!TokenKind.isPrimitiveType(tokenKind)) {
            throw new ExpectedError(parser, "primitive type", tokenKind);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        return new DeclarationStatment(tokenKind, identifier, null);
    }

    // --------------
    // parse assignment
    // --------------
    private static AssignmentStatment parseAssignment(Parser parser) throws ExpectedError {
        var identifier = parser.advance();
        if (identifier.kind() != TokenKind.IDENTIFIER) {
            throw new ExpectedError(parser, "identifier", identifier);
        }
        var equal = parser.advance();
        if (!TokenKind.isAssignment(equal)) {
            throw new ExpectedError(parser, "=", equal);
        }
        var expression = parseExpression(parser);
        parser.expect(TokenKind.SEMI);
        return new AssignmentStatment(identifier, equal, expression);
    }

    // --------------
    // parse unary operation
    // --------------
    private static ExpressionStatment parseUnaryOperation(Parser parser) throws ExpectedError {
        var first = parser.currentToken();
        if (TokenKind.isUnaryOperation(first)) {
            return parsePrefixOperation(parser);
        } else {
            return parsePostfixOperation(parser);
        }
    }

    private static ExpressionStatment parsePostfixOperation(Parser parser) throws ExpectedError {
        var first = parser.expect(TokenKind.IDENTIFIER);
        var operation = parser.advance();
        if (!TokenKind.isUnaryOperation(operation)) {
            throw new ExpectedError(parser, "postfix operation", operation);
        }
        parser.expect(TokenKind.SEMI);
        return new ExpressionStatment(new UnaryOperationExpression(first, operation, false));
    }

    private static ExpressionStatment parsePrefixOperation(Parser parser) throws ExpectedError {
        var operation = parser.advance();
        if (!TokenKind.isUnaryOperation(operation)) {
            throw new ExpectedError(parser, "prefix operation", operation);
        }
        var identifier = parser.advance();
        if (identifier.kind() != TokenKind.IDENTIFIER) {
            throw new ExpectedError(parser, "identifier", identifier);
        }
        var semi = parser.advance();
        if (semi.kind() != TokenKind.SEMI) {
            throw new ExpectedError(parser, ";", semi);
        }
        return new ExpressionStatment(new UnaryOperationExpression(identifier, operation, true));
    }

    // private static Statment parseFunctionStatment(Parser parser) throws
    // ExpectedError {
    // var identifier = parser.advance();
    // if (identifier.kind() != TokenKind.IDENTIFIER) {
    // throw new ExpectedError(parser, "identifier", identifier);
    // }
    // var openParent = parser.advance();
    // if (openParent.kind() != TokenKind.OPEN_PAREN) {
    // throw new ExpectedError(parser, "(", openParent);
    // }
    // var closeParent = parser.advance();
    // if (closeParent.kind() != TokenKind.CLOSE_PAREN) {
    // throw new ExpectedError(parser, ")", closeParent);
    // }
    // return new ExpressionStatment(new FunctionExpression(identifier));
    // }

    // --------------
    // parse control flow
    // --------------
    private static ContolFlowStatment parseControlFlowStatment(Parser parser) {
        var token = parser.currentToken();
        switch (token.kind()) {
            case WHILE -> {
                return parseWhileStatment(parser);
            }
            case IF -> {
                return parseIfStatment(parser);
            }
            case ELSE -> {
                parser.expect(TokenKind.IF);
                return null;
            }
            case FOR -> {
                return parseForStatment(parser);
            }
            case DO -> {
                return parseDoStatment(parser);
            }
            default -> {
                throw new UnsupportedOperationException("Unimplemented method 'parseControlFlowStatment'");
            }
        }
    }

    private static WhileStatment parseWhileStatment(Parser parser) {
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parse(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new WhileStatment(expression, body);
    }

    private static IfStatment parseIfStatment(Parser parser) {
        // if
        parser.expect(TokenKind.IF);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parse(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        if (parser.currentTokenKind() != TokenKind.ELSE) {
            return new IfStatment(expression, body);
        }
        // else
        parser.advance();
        // if again
        if (parser.currentTokenKind() == TokenKind.IF) {
            var elseIfBody = parseIfStatment(parser);
            var elseBody = new BlockStatment(List.of(elseIfBody));
            return new IfStatment(expression, body, elseBody);
        }
        parser.expect(TokenKind.OPEN_CURLY);
        var elseBody = parse(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new IfStatment(expression, body, elseBody);
    }

    private static ForStatment parseForStatment(Parser parser) {
        parser.expect(TokenKind.FOR);
        parser.expect(TokenKind.OPEN_PAREN);
        // foreach
        if (parser.getToken(2).kind() == TokenKind.COLON) {
            var stat = List.of(parseUniqueVariableStatment(parser));
            parser.expect(TokenKind.COLON);
            var collection = parser.expect(TokenKind.IDENTIFIER);
            parser.expect(TokenKind.CLOSE_PAREN);
            parser.expect(TokenKind.OPEN_CURLY);
            var body = parse(parser);
            parser.expect(TokenKind.CLOSE_CURLY);
            return new ForStatment(stat, collection, body);
        }
        // for
        var stat = parseVariableStatment(parser);
        var condition = parseExpression(parser);
        parser.expect(TokenKind.SEMI);
        var increment = parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parse(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new ForStatment(stat, condition, increment, body);
    }

    private static DoStatment parseDoStatment(Parser parser) {
        parser.expect(TokenKind.DO);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parse(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.SEMI);
        return new DoStatment(body, expression);
    }
}
