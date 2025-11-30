package com.compiler.parser.handlers;

import com.compiler.ast.Statement;
import com.compiler.parser.Parser;

@FunctionalInterface
public interface StatementHandler {
    Statement handle(Parser parser);
}
