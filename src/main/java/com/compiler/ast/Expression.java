package com.compiler.ast;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public interface Expression {
    public void validateType(Type type, BlockStatement parent);
    public Token getToken();
    public boolean isBoolean(BlockStatement parent);
    public boolean isNumber(BlockStatement parent);
    public boolean isDeclared(BlockStatement parent);
}
