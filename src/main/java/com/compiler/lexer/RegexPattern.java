package com.compiler.lexer;

import java.util.regex.Pattern;

public record RegexPattern(Pattern regex, RegexHandler handler) {
    
}
