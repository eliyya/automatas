package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.ast.expressions.UnaryOperationExpression;
import com.compiler.ast.statments.AssignmentStatment;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
import com.compiler.ast.statments.WhileStatment;
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

    public static BlockStatment parse(boolean inner, Parser parser) throws ExpectedError {
        var body = new ArrayList<Statment>();

        while (parser.hasTokens()) {
            if (inner && parser.currentTokenKind() == TokenKind.CLOSE_CURLY) break;
            var currentToken = parser.currentToken();
            // start with type
            if (TokenKind.isPrimitiveType(currentToken)) {
                for (var statment : parseStatment(parser)) {
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
                    var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
                    var semi = parser.advance();
                    if (semi.kind() != TokenKind.SEMI) {
                        throw new ExpectedError(parser, ";", semi);
                    }
                    body.add(new ExpressionStatment(expression));
                }
                // start with unary operation
            } else if (TokenKind.isUnaryOperation(currentToken.kind())) {
                body.add(parseUnaryOperation(parser));
            } else if (TokenKind.isCiclic(currentToken.kind())) {
                body.add(parseCiclStatment(parser));
            } else {
                var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
                var semi = parser.advance();
                if (semi.kind() != TokenKind.SEMI) {
                    throw new ExpectedError(parser, ";", semi);
                }
                body.add(new ExpressionStatment(expression));
            }
        }

        return new BlockStatment(body);
    }

    public Token currentToken() {
        return tokens.get(position);
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

    // --------------
    // parse statment
    // --------------
    private static List<DeclarationStatment> parseStatment(Parser parser) throws ExpectedError {
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
            var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
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

    private static AssignmentStatment parseAssignment(Parser parser) throws ExpectedError {
        var identifier = parser.advance();
        if (identifier.kind() != TokenKind.IDENTIFIER) {
            throw new ExpectedError(parser, "identifier", identifier);
        }
        var equal = parser.advance();
        if (!TokenKind.isAssignment(equal)) {
            throw new ExpectedError(parser, "=", equal);
        }
        var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
        parser.expect(TokenKind.SEMI);
        return new AssignmentStatment(identifier, equal, expression);
    }

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

    private static Statment parseCiclStatment(Parser parser) {
        var token = parser.currentToken();
        if (token.kind() == TokenKind.WHILE) {
            return parseWhileStatment(parser);
        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parseReservedKeyword'");
    }

    private static WhileStatment parseWhileStatment(Parser parser) {
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parse(true, parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new WhileStatment(expression, body);
    }
}
