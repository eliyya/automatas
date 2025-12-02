package com.compiler.ast;

import com.compiler.ast.statements.BlockStatement;
import com.compiler.lexer.Token;

public interface Expression {
    public void validateType(Token type, BlockStatement parent);
}
