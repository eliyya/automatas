package com.compiler.ast;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public interface Statement {
    public void validate(BlockStatement parent);
    public Token token();
}
