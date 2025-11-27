package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class Parser {
    List<Token> tokens;
    int position;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public static BlockStatment parse(List<Token> tokens) {
        var body = new ArrayList<Statment>();
        var parser = new Parser(tokens);

        while (parser.hasTokens()) {
            var currentTokenn = parser.currentToken();
            if (TokenKind.isPrimitiveType(currentTokenn)) {
                for (var statment : parseStatment(parser))
                    body.add(statment);
            } else {
                var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
                var semi = parser.advance();
                if (semi.kind() != TokenKind.SEMI) {
                    throw new RuntimeException("Expected semicolon but found " + semi.kind());
                }
                body.add(new ExpressionStatment(expression));
            }
        }

        return new BlockStatment(body);
    }

    private Token currentToken() {
        return this.tokens.get(this.position);
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
    private static List<DeclarationStatment> parseStatment(Parser parser) {
        var declarations = new ArrayList<DeclarationStatment>();
        var tokenKind = parser.advance();
        if (!TokenKind.isPrimitiveType(tokenKind)) {
            throw new RuntimeException("Expected primitive type but found " + tokenKind);
        }
        while (true) {
            var identifier = parser.advance();
            if (identifier.kind() != TokenKind.IDENTIFIER) {
                throw new RuntimeException("Expected identifier but found " + identifier.kind());
            }
            var equal = parser.advance();
            if (equal.kind() == TokenKind.SEMI) {
                declarations.add(new DeclarationStatment(tokenKind, identifier, null));
                return declarations;
            }
            if (!(equal.kind() == TokenKind.ASSIGNMENT || equal.kind() == TokenKind.COMMA)) {
                throw new RuntimeException("Expected semicolon but found " + equal.kind());
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
                throw new RuntimeException("Expected semicolon but found " + semi.kind());
            }
            return declarations;
        }
    }
}
