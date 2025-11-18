package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.ast.statments.BlockStatment;
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
            body.add(Parser.parseStatment(parser));
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
    private static Statment parseStatment(Parser parser) {
        var tokenKind = parser.currentTokenKind();
        var handler = PrattRegistry.stmtLU.get(tokenKind);
        if (handler != null) {
            return handler.handle(parser);
        }
        var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
        parser.expect(TokenKind.SEMI);
        return new ExpressionStatment(expression);
    }

    private Token expectError(TokenKind expectedKind) {
        return this.expectError(expectedKind, "Expected " + expectedKind + " but found " + this.currentToken().kind());
    }

    private Token expectError(TokenKind expectedKind, String err) {
        var token = this.currentToken();
        var kind = token.kind();
        if (kind != expectedKind) {
            throw new RuntimeException(err);
        }
        return this.advance();
    }

    private Token expect(TokenKind expectKind) {
        return this.expectError(expectKind);
    }
}
