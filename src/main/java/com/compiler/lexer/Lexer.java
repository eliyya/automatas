package com.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Lexer {
    List<Token> tokens = new ArrayList<>();
    String source = "";
    int pos = 0;
    int row = 1;
    int col = 0;
    float colFloat = 01f;
    RegexPattern[] patterns = {
            new RegexPattern(Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*"), identifierHandler()),
            new RegexPattern(Pattern.compile("[+-]?0[\\d_]*\\d+([fFdD])?"), literalHandler(TokenKind.OCTAL_EXPRESSION)),
            new RegexPattern(Pattern.compile("[+-]?0[xX][0-9a-fA-F]([0-9a-fA-F_]*([0-9a-fA-F]|(\\.[0-9a-fA-F]+[Pp][+-]?[0-9]+)))?([fFdD])?"), literalHandler(TokenKind.HEXADECIMAL_EXPRESSION)),
            new RegexPattern(Pattern.compile("[+-]?0[Bb][01]([01_]*[01])?([fFdD])?"), literalHandler(TokenKind.BINARY_EXPRESSION)),
            new RegexPattern(Pattern.compile("([0-9]+(\\.[0-9]*)?|\\.[0-9]+)([eE][+-]?[0-9]+)?[fFdD]?|(0[xX][0-9a-fA-F]+|0[bB][01]+|[0-9]+)[lL]?"), literalHandler(TokenKind.NUMBER_EXPRESSION)),
            new RegexPattern(Pattern.compile("\".*?\""), literalHandler(TokenKind.STRING_EXPRESSION)),
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
            new RegexPattern(Pattern.compile("\\+="), defaultHandler(TokenKind.PLUS_ASSIGNMENT, "+=")),
            new RegexPattern(Pattern.compile("-="), defaultHandler(TokenKind.MINUS_ASSIGNMENT, "-=")),
            new RegexPattern(Pattern.compile("\\*="), defaultHandler(TokenKind.STAR_ASSIGNMENT, "*=")),
            new RegexPattern(Pattern.compile("/="), defaultHandler(TokenKind.SLASH_ASSIGNMENT, "/=")),
            new RegexPattern(Pattern.compile("%="), defaultHandler(TokenKind.PERCENT_ASSIGNMENT, "%=")),

            new RegexPattern(Pattern.compile("="), defaultHandler(TokenKind.ASSIGNMENT, "=")),
            new RegexPattern(Pattern.compile("!"), defaultHandler(TokenKind.NOT, "!")),
            new RegexPattern(Pattern.compile("<"), defaultHandler(TokenKind.LESS, "<")),
            new RegexPattern(Pattern.compile(">"), defaultHandler(TokenKind.GREATER, ">")),
            new RegexPattern(Pattern.compile("\\."), defaultHandler(TokenKind.DOT, ".")),
            new RegexPattern(Pattern.compile(";"), defaultHandler(TokenKind.SEMI, ";")),
            new RegexPattern(Pattern.compile(":"), defaultHandler(TokenKind.COLON, ":")),
            new RegexPattern(Pattern.compile("\\?"), defaultHandler(TokenKind.QUESTION, "?")),
            new RegexPattern(Pattern.compile(","), defaultHandler(TokenKind.COMMA, ",")),
            new RegexPattern(Pattern.compile("\\+"), defaultHandler(TokenKind.PLUS, "+")),
            new RegexPattern(Pattern.compile("-"), defaultHandler(TokenKind.MINUS, "-")),
            new RegexPattern(Pattern.compile("/"), defaultHandler(TokenKind.SLASH, "/")),
            new RegexPattern(Pattern.compile("\\*"), defaultHandler(TokenKind.STAR, "*")),
            new RegexPattern(Pattern.compile("%"), defaultHandler(TokenKind.PERCENT, "%")),
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
                var lines = lexer.source.lines().toList();
                var line = lines.get(lexer.row - 1);
                IO.println("");
                IO.println(line);
                IO.println(" ".repeat(lexer.col) + "^");
                throw new RuntimeException("\u001B[31mLexer:Error ->\u001B[0m unrecognized token `\u001B[32m"
                        + lexer.reminder().substring(0, 1) + "\u001B[0m` at \u001B[34mline " + lexer.row
                        + "\u001B[0m and \u001B[34mcolumn " + lexer.col + "\u001B[0m");
            }
        }

        lexer.push(new Token(TokenKind.EOF, "EOF", lexer.row, lexer.col));

        return lexer.tokens;
    }

    private void advanceN(int n) {
        String consumed = this.source.substring(this.pos, this.pos + n);

        for (int i = 0; i < consumed.length(); i++) {
            char c = consumed.charAt(i);
            if (c == '\n') {
                row++;
                col = 0;
            } else {
                col++;
            }
        }

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
            int row = lexer.row;
            int col = lexer.col;

            lexer.advanceN(value.length());
            lexer.push(new Token(kind, value, row, col));
        };
    }

    private static RegexHandler literalHandler(TokenKind kind) {
        return (lexer, regex) -> {
            var match = regex.matcher(lexer.reminder());
            if (match.find() && match.start() == 0) {
                int row = lexer.row;
                int col = lexer.col;
                lexer.advanceN(match.end());
                var value = switch (kind) {
                    case STRING_EXPRESSION -> match.group().substring(1, match.group().length() - 1);
                    case CHAR -> match.group().substring(1, match.group().length() - 1);
                    default -> match.group();
                };
                if (TokenKind.isNumberExpression(kind)) {
                    if (value.endsWith("f") || value.endsWith("F")) {
                        if (kind == TokenKind.HEXADECIMAL_EXPRESSION) {
                            if (value.contains(".")) {
                                lexer.push(new Token(TokenKind.FLOAT_EXPRESSION, value, row, col));
                            } else {
                                lexer.push(new Token(kind, value, row, col));
                            }
                        } else {
                            lexer.push(new Token(TokenKind.FLOAT_EXPRESSION, value, row, col));
                        }
                    }else {
                        lexer.push(new Token(kind, value, row, col));
                    }
                } else {
                    lexer.push(new Token(kind, value, row, col));
                }
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
                    lexer.push(new Token(TokenKind.fromText(value), value, lexer.row, lexer.col));
                } else {
                    lexer.push(new Token(TokenKind.IDENTIFIER, value, lexer.row, lexer.col));
                }
                lexer.advanceN(match.end());
            }
        };
    }
}