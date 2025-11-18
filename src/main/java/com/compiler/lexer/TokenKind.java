package com.compiler.lexer;

import java.util.HashMap;
import java.util.Map;

public enum TokenKind {
	EOF("EOF"),
	NULL("null"),
	TRUE("true"),
	FALSE("false"),
	NUMBER("number"),
	STRING("string"),
	CHAR("char"),
	BOOLEAN("boolean"),
	IDENTIFIER("identifier"),
	// types
	VOID("void"),
	SHORT("short"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),
	BYTE("byte"),
	INT("int"),

	// Grouping & Braces
	OPEN_BRACKET("["),
	CLOSE_BRACKET("]"),
	OPEN_CURLY("{"),
	CLOSE_CURLY("}"),
	OPEN_PAREN("("),
	CLOSE_PAREN(")"),

	// Equivilance
	ASSIGNMENT("="),
	EQUALS("=="),
	NOT_EQUALS("!="),
	NOT("!"),

	// Conditional
	LESS("<"),
	LESS_EQUALS("<="),
	GREATER(">"),
	GREATER_EQUALS(">="),

	// Logical
	OR("||"),
	AND("&&"),

	// Symbols
	DOT("."),
	SEMI_COLON(";"),
	COLON(":"),
	QUESTION("?"),
	COMMA(","),

	// Shorthand
	PLUS_PLUS("++"),
	MINUS_MINUS("--"),
	PLUS_EQUALS("+="),
	MINUS_EQUALS("-="),
	STAR_EQUALS("*="),
	SLASH_EQUALS("/="),
	PERCENT_EQUALS("%="),

	// Maths
	PLUS("+"),
	DASH("-"),
	SLASH("/"),
	STAR("*"),
	PERCENT("%"),

	// Reserved Keywords
	VAR("var"),
	CLASS("class"),
	NEW("new"),
	IMPORT("import"),
	PACKAGE("package"),
	IF("if"),
	ELSE("else"),
	WHILE("while"),
	FOR("for"),
	DO("do"),
	INSTANCEOF("instanceof"),

	// misc
	AT("@");

	private static final Map<String, TokenKind> lookup = new HashMap<>();

	static {
		for (var kind : TokenKind.values()) {
			lookup.put(kind.text(), kind);
		}
	}

	private final String text;

	private TokenKind(String text) {
		this.text = text;
	}

	public String text() {
		return this.text;
	}

	public static TokenKind fromText(String text) {
		return lookup.get(text);
	}
}
