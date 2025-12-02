package com.compiler.parser;

import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.errors.ExpectedError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class Parser {
    List<Token> tokens;
    int position;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public static BlockStatement parse(List<Token> tokens) {
        var parser = new Parser(tokens);
        return StatementParser.parseBlockStatement(false, parser);
    }

    public static Expression parseExpression(Parser parser) {
        return ExpressionParser.parseExpression(parser, BindingPower.DEFAULT_BP);
    }

    public Token currentToken() {
        return tokens.get(position);
    }

    public Token getToken(int n) {
        return tokens.get(position + n);
    }

    public Token nextToken() {
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

    public boolean hasTokens() {
        return this.position < this.tokens.size() && this.currentTokenKind() != TokenKind.EOF;
    }

    public Token expect(TokenKind kind, String expect) {
        var token = this.advance();
        if (token.kind() != kind) {
            throw new ExpectedError(expect, token);
        }
        return token;
    }

    public Token expect(TokenKind kind) {
        return this.expect(kind, kind.text());
    }

}
