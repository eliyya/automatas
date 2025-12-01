package com.compiler.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

record RegexPattern(Pattern regex, Consumer<Pattern> handler) {}

public class Lexer {
    List<Token> tokens = new ArrayList<>();
    String source = "";
    int pos = 0;
    int row = 1;
    int col = 0;
    float colFloat = 01f;
    RegexPattern[] patterns = {
            // espacios
            new RegexPattern(Pattern.compile("\\s+"), skip()),
            // comentarios
            new RegexPattern(Pattern.compile("//.*"), skip()),
            new RegexPattern(Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL), skip()),
            // binary
            new RegexPattern(Pattern.compile("[+-]?0[Bb][01]([01_]*[01])?[fFdDLl]?"), handler(TokenKind.NUMBER_EXPRESSION)),
            // octal
            new RegexPattern(Pattern.compile("[+-]?0[0-7_]*[0-7]+[fFdDLl]?"), handler(TokenKind.NUMBER_EXPRESSION)),
            // hex
            new RegexPattern(Pattern.compile("[+-]?0[xX](([0-9a-fA-F][0-9a-fA-F_]*[0-9a-fA-F]+)|[0-9a-fA-F]+)(\\.(([0-9a-fA-F][0-9a-fA-F_]*[0-9a-fA-F]+)|[0-9a-fA-F]+)[Pp][+-]?[0-9]+)?([fFdDdLl])?"), handler(TokenKind.NUMBER_EXPRESSION)),
            // decoimal
            new RegexPattern(Pattern.compile("[+-]?((\\d[\\d_]*\\d+)|(\\d+))(\\.((\\d[\\d_]*\\d+)|(\\d+))?)?[fFdDdLl]?"), handler(TokenKind.NUMBER_EXPRESSION)),
            // integer
            new RegexPattern(Pattern.compile("[+-]?((\\d[\\d_]*\\d+)|(\\d+))[fFdDLl]?"), handler(TokenKind.NUMBER_EXPRESSION)),
            new RegexPattern(Pattern.compile("\".*?\""), handler(TokenKind.STRING_EXPRESSION)),
            new RegexPattern(Pattern.compile("'.*?'"), handler(TokenKind.CHAR)),
            new RegexPattern(Pattern.compile("\\["), handler(TokenKind.OPEN_BRACKET)),
            new RegexPattern(Pattern.compile("\\]"), handler(TokenKind.CLOSE_BRACKET)),
            new RegexPattern(Pattern.compile("\\{"), handler(TokenKind.OPEN_CURLY)),
            new RegexPattern(Pattern.compile("\\}"), handler(TokenKind.CLOSE_CURLY)),
            new RegexPattern(Pattern.compile("\\("), handler(TokenKind.OPEN_PAREN)),
            new RegexPattern(Pattern.compile("\\)"), handler(TokenKind.CLOSE_PAREN)),
            // double
            new RegexPattern(Pattern.compile("=="), handler(TokenKind.EQUALS)),
            new RegexPattern(Pattern.compile("!="), handler(TokenKind.NOT_EQUALS)),
            new RegexPattern(Pattern.compile("<="), handler(TokenKind.LESS_EQUALS)),
            new RegexPattern(Pattern.compile(">="), handler(TokenKind.GREATER_EQUALS)),
            new RegexPattern(Pattern.compile("\\|\\|"), handler(TokenKind.OR)),
            new RegexPattern(Pattern.compile("&&"), handler(TokenKind.AND)),
            new RegexPattern(Pattern.compile("\\+\\+"), handler(TokenKind.PLUS_PLUS)),
            new RegexPattern(Pattern.compile("--"), handler(TokenKind.MINUS_MINUS)),
            new RegexPattern(Pattern.compile("\\+="), handler(TokenKind.PLUS_ASSIGNMENT)),
            new RegexPattern(Pattern.compile("-="), handler(TokenKind.MINUS_ASSIGNMENT)),
            new RegexPattern(Pattern.compile("\\*="), handler(TokenKind.STAR_ASSIGNMENT)),
            new RegexPattern(Pattern.compile("/="), handler(TokenKind.SLASH_ASSIGNMENT)),
            new RegexPattern(Pattern.compile("%="), handler(TokenKind.PERCENT_ASSIGNMENT)),
            // single
            new RegexPattern(Pattern.compile("="), handler(TokenKind.ASSIGNMENT)),
            new RegexPattern(Pattern.compile("!"), handler(TokenKind.NOT)),
            new RegexPattern(Pattern.compile("<"), handler(TokenKind.LESS)),
            new RegexPattern(Pattern.compile(">"), handler(TokenKind.GREATER)),
            new RegexPattern(Pattern.compile("\\."), handler(TokenKind.DOT)),
            new RegexPattern(Pattern.compile(";"), handler(TokenKind.SEMI)),
            new RegexPattern(Pattern.compile(":"), handler(TokenKind.COLON)),
            new RegexPattern(Pattern.compile("\\?"), handler(TokenKind.QUESTION)),
            new RegexPattern(Pattern.compile(","), handler(TokenKind.COMMA)),
            new RegexPattern(Pattern.compile("\\+"), handler(TokenKind.PLUS)),
            new RegexPattern(Pattern.compile("-"), handler(TokenKind.MINUS)),
            new RegexPattern(Pattern.compile("/"), handler(TokenKind.SLASH)),
            new RegexPattern(Pattern.compile("\\*"), handler(TokenKind.STAR)),
            new RegexPattern(Pattern.compile("%"), handler(TokenKind.PERCENT)),
            // types
            new RegexPattern(Pattern.compile("float"), handler(TokenKind.FLOAT)),
            new RegexPattern(Pattern.compile("double"), handler(TokenKind.DOUBLE)),
            new RegexPattern(Pattern.compile("int"), handler(TokenKind.INT)),
            new RegexPattern(Pattern.compile("short"), handler(TokenKind.SHORT)),
            new RegexPattern(Pattern.compile("long"), handler(TokenKind.LONG)),
            new RegexPattern(Pattern.compile("byte"), handler(TokenKind.BYTE)),
            new RegexPattern(Pattern.compile("char"), handler(TokenKind.CHAR)),
            new RegexPattern(Pattern.compile("boolean"), handler(TokenKind.BOOLEAN)),
            new RegexPattern(Pattern.compile("String"), handler(TokenKind.STRING)),
            new RegexPattern(Pattern.compile("void"), handler(TokenKind.VOID)),
            new RegexPattern(Pattern.compile("var"), handler(TokenKind.VAR)),
            new RegexPattern(Pattern.compile("true"), handler(TokenKind.TRUE)),
            new RegexPattern(Pattern.compile("false"), handler(TokenKind.FALSE)),
            new RegexPattern(Pattern.compile("null"), handler(TokenKind.NULL)),
            // control flow
            new RegexPattern(Pattern.compile("if"), handler(TokenKind.IF)),
            new RegexPattern(Pattern.compile("else"), handler(TokenKind.ELSE)),
            new RegexPattern(Pattern.compile("while"), handler(TokenKind.WHILE)),
            new RegexPattern(Pattern.compile("for"), handler(TokenKind.FOR)),
            new RegexPattern(Pattern.compile("do"), handler(TokenKind.DO)),
            new RegexPattern(Pattern.compile("switch"), handler(TokenKind.SWITCH)),
            new RegexPattern(Pattern.compile("break"), handler(TokenKind.BREAK)),
            new RegexPattern(Pattern.compile("return"), handler(TokenKind.RETURN)),
            // identifier
            new RegexPattern(Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*"), handler(TokenKind.IDENTIFIER)),
    };

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!this.atEOF()) {
            var matched = false;

            for (var pattern : this.patterns) {
                var match = pattern.regex().matcher(this.reminder());
                if (match.find() && match.start() == 0) {
                    pattern.handler().accept(pattern.regex());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                var lines = this.source.lines().toList();
                var line = lines.get(this.row - 1);
                IO.println("");
                IO.println(line);
                IO.println(" ".repeat(this.col) + "^");
                throw new RuntimeException("\u001B[31mLexer:Error ->\u001B[0m unrecognized token `\u001B[32m"
                        + this.reminder().substring(0, 1) + "\u001B[0m` at \u001B[34mline " + this.row
                        + "\u001B[0m and \u001B[34mcolumn " + this.col + "\u001B[0m");
            }
        }

        this.push(new Token(TokenKind.EOF, "EOF", this.row, this.col));

        return this.tokens;
    }

    private void advance(int n) {
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

    private Consumer<Pattern> handler(TokenKind kind) {
        return (regex) -> {
            var match = regex.matcher(this.reminder());
            if (match.find() && match.start() == 0) {
                this.advance(match.end());
                var value = switch (kind) {
                    case STRING_EXPRESSION -> match.group().substring(1, match.group().length() - 1);
                    case CHAR -> match.group().substring(1, match.group().length() - 1);
                    default -> match.group();
                };
                this.push(new Token(kind, value, this.row, this.col));
            }
        };
    }

    private Consumer<Pattern> skip() {
        return (pattern) -> {
            var match = pattern.matcher(reminder());
            if (match.find() && match.start() == 0) {
                advance(match.end());
            }
        };
    }
}