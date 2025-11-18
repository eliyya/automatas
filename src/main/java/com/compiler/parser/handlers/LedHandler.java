package com.compiler.parser.handlers;

import com.compiler.ast.Expression;
import com.compiler.parser.BindingPower;
import com.compiler.parser.Parser;

@FunctionalInterface
public interface LedHandler {
    Expression handle(Parser parser, Expression left, BindingPower bp);
}
