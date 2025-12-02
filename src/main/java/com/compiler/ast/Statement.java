package com.compiler.ast;

import com.compiler.ast.statements.BlockStatement;

public interface Statement {
    public void validate(BlockStatement parent);
}
