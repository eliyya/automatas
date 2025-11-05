package com.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.compiler.utils.TokenType;

public class Tokenizer {
    public final static List<String> TYPES = List.of("int", "float", "double", "char", "String", "boolean");

    private static final Set<Character> singleCharTokens = Set.of(';', '(', ')', '{', '}', ',');

    public final List<Token> tokens;

    private Tokenizer(ArrayList<Token> tokens) {
        this.tokens = List.copyOf(tokens);
    }

    public static Tokenizer tokenize(File file) throws FileNotFoundException {
        var scanner = new Scanner(file);
        var logger = new Logger(new File("./logs.log")).clear();

        var lineCount = 0;
        var str = new StringBuilder();
        var tokens = new ArrayList<Token>();

        while (scanner.hasNextLine()) {
            var whileTokens = new ArrayList<Token>();
            lineCount++;
            var line = scanner.nextLine();
            str.setLength(0);

            logger.log(lineCount + ":\t");
            for (int i = 0; i < line.length(); i++) {

                var character = line.charAt(i);

                if (Character.isWhitespace(character)) {
                    if (!str.isEmpty()) {
                        whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                        str.setLength(0);
                    }
                } else if (singleCharTokens.contains(character)) {
                    if (!str.isEmpty()) {
                        whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                        str.setLength(0);
                    }
                    str.append(character);
                    whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i, line));
                    str.setLength(0);

                } else if (character == '=') {

                    if (!str.isEmpty()) {
                        logger.log(str.toString() + "•");
                        whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                        str.setLength(0);
                    }

                    str.append(character);

                    if (i + 1 < line.length() && line.charAt(i + 1) == '=') {
                        str.append(line.charAt(++i));
                    }

                    whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                    str.setLength(0);
                } else if (character == '"') {
                    if (!str.isEmpty()) {
                        whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                        str.setLength(0);
                    }
                    str.append(character);
                    i++;
                    while (i < line.length() && line.charAt(i) != '"') {
                        str.append(line.charAt(i++));
                    }
                    if (i < line.length())
                        str.append('"');
                    whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                    str.setLength(0);
                    continue;
                } else if (character == '\'') {
                    if (!str.isEmpty()) {
                        whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                        str.setLength(0);
                    }
                    str.append(character);
                    i++;
                    while (i < line.length() && line.charAt(i) != '\'') {
                        str.append(line.charAt(i++));
                    }
                    if (i < line.length())
                        str.append('\'');
                    whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                    str.setLength(0);
                    continue;
                } else {
                    str.append(character);
                }

                if (i == line.length() - 1) {
                    if (!str.isEmpty()) {
                        whileTokens.add(new Token(str.toString(), getTokenType(str.toString()), lineCount, i - str.length(), line));
                        str.setLength(0);
                    }
                }
            }

            whileTokens.add(new Token("", TokenType.EOF, lineCount, line.length(), line));
            logger.logTokens(whileTokens);
            tokens.addAll(whileTokens);
            whileTokens.clear();
        }

        scanner.close();
        return new Tokenizer(tokens);
    }

    private static TokenType getTokenType(String value) {
        if (Tokenizer.TYPES.contains(value)) {
            return TokenType.TYPE;
        }
        if (Character.isAlphabetic(value.charAt(0)) || value.charAt(0) == '_' || value.charAt(0) == '$') {
            return TokenType.IDENTIFICATOR;
        }
        return TokenType.UNKNOWN;
    }
}
