package com.compiler.parser.handlers;

import com.compiler.ast.Expression;
import com.compiler.parser.Parser;

@FunctionalInterface
public interface NudHandler {
    Expression handle(Parser parser);
}
