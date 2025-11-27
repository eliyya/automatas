package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Statment;
import com.compiler.ast.statments.AssignmentStatment;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class Parser {
    List<String> lines;
    List<Token> tokens;
    int position;

    private Parser(List<Token> tokens, List<String> lines) {
        this.tokens = tokens;
        this.lines = lines;
        this.position = 0;
    }

    public static BlockStatment parse(List<Token> tokens, List<String> lines) {
        var body = new ArrayList<Statment>();
        var parser = new Parser(tokens, lines);

        while (parser.hasTokens()) {
            var currentToken = parser.currentToken();
            if (TokenKind.isPrimitiveType(currentToken)) {
                for (var statment : parseStatment(parser))
                    body.add(statment);
            } else if (currentToken.kind() == TokenKind.IDENTIFIER) {
                var nextToken = parser.nextToken();
                if (nextToken.kind() == TokenKind.ASSIGNMENT) {
                    body.add(parseAssignment(parser));
                } else {
                    var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
                    var semi = parser.advance();
                    if (semi.kind() != TokenKind.SEMI) {
                        parser.throwsExpectedError(";", semi);
                    }
                    body.add(new ExpressionStatment(expression));
                }
            } else {
                var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
                var semi = parser.advance();
                if (semi.kind() != TokenKind.SEMI) {
                    parser.throwsExpectedError(";", semi);
                }
                body.add(new ExpressionStatment(expression));
            }
        }

        return new BlockStatment(body);
    }

    private Token currentToken() {
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
    private static List<DeclarationStatment> parseStatment(Parser parser) {
        var declarations = new ArrayList<DeclarationStatment>();
        var tokenKind = parser.advance();
        if (!TokenKind.isPrimitiveType(tokenKind)) {
            parser.throwsExpectedError("primitive type", tokenKind);
        }
        while (true) {
            var identifier = parser.advance();
            if (identifier.kind() != TokenKind.IDENTIFIER) {
                parser.throwsExpectedError("identifier", identifier);
            }
            var equal = parser.advance();
            if (equal.kind() == TokenKind.SEMI) {
                declarations.add(new DeclarationStatment(tokenKind, identifier, null));
                return declarations;
            }
            if (!(equal.kind() == TokenKind.ASSIGNMENT || equal.kind() == TokenKind.COMMA)) {
                parser.throwsExpectedError(";", equal);
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
                parser.throwsExpectedError(";", semi);
            }
            return declarations;
        }
    }

    private static AssignmentStatment parseAssignment(Parser parser) {
        var identifier = parser.advance();
        if (identifier.kind() != TokenKind.IDENTIFIER) {
            parser.throwsExpectedError("identifier", identifier);
        }
        var equal = parser.advance();
        if (!TokenKind.isAssignment(equal)) {
            parser.throwsExpectedError("=", equal);
        }
        var expression = PrattRegistry.parseExpression(parser, BindingPower.DEFAULT_BP);
        var semi = parser.advance();
        if (semi.kind() != TokenKind.SEMI) {
            parser.throwsExpectedError(";", semi);
        }
        return new AssignmentStatment(identifier, equal, expression);
    }

    private static String getLine(Parser parser, int lineNumber) {
        if (lineNumber < 1 || lineNumber > parser.lines.size()) {
            return null;
        }
        return parser.lines.get(lineNumber - 1);
    }

    private void throwsExpectedError(String expected, Token found) {
        System.out.println("");
        var line = getLine(this, found.line());
        System.out.println(line);
        System.out.println(" ".repeat(found.column()) + "^");
        throw new RuntimeException("\u001B[31mParser:Error ->\u001B[0m Expected `\u001B[32m" + expected + "\u001B[0m` but found `\u001B[32m" + found.value() + "\u001B[0m` at \u001B[34mline "
                + found.line() + "\u001B[0m and \u001B[34mcolumn " + found.column()+"\u001B[0m");
    }

}
