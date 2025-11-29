package com.compiler.parser.handlers;

import com.compiler.ast.Statment;
import com.compiler.parser.Parser;

@FunctionalInterface
public interface StatmentHandler {
    Statment handle(Parser parser);
}
