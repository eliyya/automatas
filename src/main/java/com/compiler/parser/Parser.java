package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.ast.statments.AssignmentStatment;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
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

    public static BlockStatment parse(List<Token> tokens, List<String> lines) throws ExpectedError {
        var body = new ArrayList<Statment>();
        var parser = new Parser(tokens, lines);

        while (parser.hasTokens()) {
            var currentToken = parser.currentToken();
            if (TokenKind.isPrimitiveType(currentToken)) {
                for (var statment : parseStatment(parser)) {
                    body.add(statment);
                }
            } else if (currentToken.kind() == TokenKind.IDENTIFIER) {
                var nextToken = parser.nextToken();
                if (TokenKind.isAssignment(nextToken.kind())) {
                    body.add(parseAssignment(parser));
                } else {
                    var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
                    var semi = parser.advance();
                    if (semi.kind() != TokenKind.SEMI) {
                        throw new ExpectedError(parser, ";", semi);
                    }
                    body.add(new ExpressionStatment(expression));
                }
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
            var identifier = parser.advance();
            if (identifier.kind() != TokenKind.IDENTIFIER) {
                throw new ExpectedError(parser, "identifier", identifier);
            }
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
        System.out.println(identifier);
        System.out.println(equal);
        System.out.println(equal.kind());
        if (!TokenKind.isAssignment(equal)) {
            throw new ExpectedError(parser, "=", equal);
        }
        var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
        var semi = parser.advance();
        if (semi.kind() != TokenKind.SEMI) {
            throw new ExpectedError(parser, ";", semi);
        }
        return new AssignmentStatment(identifier, equal, expression);
    }

}
