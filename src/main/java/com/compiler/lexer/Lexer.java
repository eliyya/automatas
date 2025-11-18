package com.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Lexer {
    List<Token> tokens = new ArrayList<>();
    String source = "";
    int pos = 0;
    RegexPattern[] patterns = {
        new RegexPattern(Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*"), identifierHandler()),
        new RegexPattern(Pattern.compile("([0-9]+(\\.[0-9]*)?|\\.[0-9]+)([eE][+-]?[0-9]+)?[fFdD]?|(0[xX][0-9a-fA-F]+|0[bB][01]+|[0-9]+)[lL]?"), literalHandler(TokenKind.NUMBER)),
        new RegexPattern(Pattern.compile("\".*?\""), literalHandler(TokenKind.STRING)),
        new RegexPattern(Pattern.compile("'.*?'"), literalHandler(TokenKind.CHAR)),
        new RegexPattern(Pattern.compile("\\s+"), skipHandler()),
        new RegexPattern(Pattern.compile("//.*"), skipHandler()),
        new RegexPattern(Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL), skipHandler()),
        new RegexPattern(Pattern.compile("\\["), defaultHandler(TokenKind.OPEN_BRACKET, "[")),
        new RegexPattern(Pattern.compile("\\]"), defaultHandler(TokenKind.CLOSE_BRACKET, "]")),
        new RegexPattern(Pattern.compile("\\{"), defaultHandler(TokenKind.OPEN_CURLY, "{")),
        new RegexPattern(Pattern.compile("\\}"), defaultHandler(TokenKind.CLOSE_CURLY, "}")),
        new RegexPattern(Pattern.compile("\\("), defaultHandler(TokenKind.OPEN_PAREN, "(")),
        new RegexPattern(Pattern.compile("\\)"), defaultHandler(TokenKind.CLOSE_PAREN, ")")),

        new RegexPattern(Pattern.compile("=="), defaultHandler(TokenKind.EQUALS, "==")),
        new RegexPattern(Pattern.compile("!="), defaultHandler(TokenKind.NOT_EQUALS, "!=")),
        new RegexPattern(Pattern.compile("<="), defaultHandler(TokenKind.LESS_EQUALS, "<=")),
        new RegexPattern(Pattern.compile(">="), defaultHandler(TokenKind.GREATER_EQUALS, ">=")),
        new RegexPattern(Pattern.compile("\\|\\|"), defaultHandler(TokenKind.OR, "||")),
        new RegexPattern(Pattern.compile("&&"), defaultHandler(TokenKind.AND, "&&")),
        new RegexPattern(Pattern.compile("\\+\\+"), defaultHandler(TokenKind.PLUS_PLUS, "++")),
        new RegexPattern(Pattern.compile("--"), defaultHandler(TokenKind.MINUS_MINUS, "--")),
        new RegexPattern(Pattern.compile("\\+="), defaultHandler(TokenKind.PLUS_EQUALS, "+=")),
        new RegexPattern(Pattern.compile("-="), defaultHandler(TokenKind.MINUS_EQUALS, "-=")),
        new RegexPattern(Pattern.compile("\\*="), defaultHandler(TokenKind.STAR_EQUALS, "*=")),
        new RegexPattern(Pattern.compile("/="), defaultHandler(TokenKind.SLASH_EQUALS, "/=")),
        new RegexPattern(Pattern.compile("%="), defaultHandler(TokenKind.PERCENT_EQUALS, "%=")),

        new RegexPattern(Pattern.compile("="), defaultHandler(TokenKind.ASSIGNMENT, "=")),
        new RegexPattern(Pattern.compile("!"), defaultHandler(TokenKind.NOT, "!")),
        new RegexPattern(Pattern.compile("<"), defaultHandler(TokenKind.LESS, "<")),
        new RegexPattern(Pattern.compile(">"), defaultHandler(TokenKind.GREATER, ">")),
        new RegexPattern(Pattern.compile("\\."), defaultHandler(TokenKind.DOT, ".")),
        new RegexPattern(Pattern.compile(";"), defaultHandler(TokenKind.SEMI_COLON, ";")),
        new RegexPattern(Pattern.compile(":"), defaultHandler(TokenKind.COLON, ":")),
        new RegexPattern(Pattern.compile("\\?"), defaultHandler(TokenKind.QUESTION, "?")),
        new RegexPattern(Pattern.compile(","), defaultHandler(TokenKind.COMMA, ",")),
        new RegexPattern(Pattern.compile("\\+"), defaultHandler(TokenKind.PLUS, "+")),
        new RegexPattern(Pattern.compile("-"), defaultHandler(TokenKind.DASH, "-")),
        new RegexPattern(Pattern.compile("/"), defaultHandler(TokenKind.SLASH, "/")),
        new RegexPattern(Pattern.compile("\\*"), defaultHandler(TokenKind.STAR, "*")),
        new RegexPattern(Pattern.compile("%"), defaultHandler(TokenKind.PERCENT, "%")),
        new RegexPattern(Pattern.compile("@"), defaultHandler(TokenKind.AT, "@")),
    };

    private Lexer(String source) {
        this.source = source;
    }

    public static List<Token> tokenize(String source) {
        var lexer = new Lexer(source);

        while (!lexer.atEOF()) {
            var matched = false;
            
            for (var pattern : lexer.patterns) {
                var match = pattern.regex().matcher(lexer.reminder());
                if (match.find() && match.start() == 0) {
                    pattern.handler().handle(lexer, pattern.regex());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new Error("Lexer:Error -> unrecognized token near " + lexer.reminder().substring(0, 20));
            }
        }

        lexer.push(new Token(TokenKind.EOF, "EOF"));
        
        return lexer.tokens;
    }

    private void advanceN(int n) {
        this.pos += n;
    }

    private void push(Token token) {
        this.tokens.add(token);
    }

    private String reminder() {
        return this.source.substring(this.pos);
    }

    private boolean atEOF() {
        return this.pos >= this.source.length();
    }

    private static RegexHandler defaultHandler(TokenKind kind, String value) {
        return (lexer, regex) -> {
            lexer.advanceN(value.length());
            lexer.push(new Token(kind, value));
        };
    }

    private static RegexHandler literalHandler(TokenKind kind) {
        return (lexer, regex) -> {
            var match = regex.matcher(lexer.reminder());
            if (match.find() && match.start() == 0) {
                lexer.advanceN(match.end());
                lexer.push(new Token(kind, match.group()));
            }
        };
    }

    private static RegexHandler skipHandler() {
        return (lexer, regex) -> {
            var match = regex.matcher(lexer.reminder());
            if (match.find() && match.start() == 0) {
                lexer.advanceN(match.end());
            }
        };
    }

    private static RegexHandler identifierHandler() {
        return (lexer, regex) -> {
            var match = regex.matcher(lexer.reminder());
            if (match.find() && match.start() == 0) {
                var value = match.group();
                if (TokenKind.isReservedKeyword(value)) {
                    lexer.push(new Token(TokenKind.fromText(value), value));
                } else {
                    lexer.push(new Token(TokenKind.IDENTIFIER, value));
                }
                lexer.advanceN(match.end());
            }
        };
    }
}