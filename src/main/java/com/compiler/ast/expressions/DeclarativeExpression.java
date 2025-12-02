package com.compiler.ast.expressions;

import com.compiler.ast.Expression;

public sealed interface DeclarativeExpression extends Expression
    permits IdentifierExpression, AssignmentExpression {

    }
