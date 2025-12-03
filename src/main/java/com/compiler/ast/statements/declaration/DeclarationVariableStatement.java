package com.compiler.ast.statements.declaration;

import java.util.List;
import java.util.stream.Collectors;

import com.compiler.ast.Type;
import com.compiler.ast.expressions.AssignmentExpression;
import com.compiler.ast.expressions.DeclarativeExpression;
import com.compiler.ast.expressions.IdentifierExpression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.DeclarationStatement;
import com.compiler.errors.DuplicateError;

public class DeclarationVariableStatement implements DeclarationStatement {
    final String _c = "DeclarationVariableStatement";
    Type type;
    List<DeclarativeExpression> identifiers;

    public DeclarationVariableStatement(Type type, List<DeclarativeExpression> identifiers) {
        this.type = type;
        this.identifiers = identifiers;
    }

    @Override
    public void validate(BlockStatement parent) {
        for (var identifier : identifiers) {
            if (identifier instanceof IdentifierExpression id) {
                if (id.isDeclared(parent)) {
                    throw new DuplicateError(id.getIdentifier());
                } else {
                    parent.addVar(id.name(), this.type);
                }
            } else if (identifier instanceof AssignmentExpression as) {
                var id = as.getIdentifier();
                if (as.isDeclared(parent)) {
                    throw new DuplicateError(id);
                } else {
                    parent.addVar(id.value(), this.type);
                    as.expression().validateType(this.type, parent);
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.type.token().value() + " " + this.identifiers.stream().map(DeclarativeExpression::toString).collect(Collectors.joining(", ")) + ";";
        // if (expression == null) {
        //     return type.value() + " " + identifier.value() + ";";
        // }
        // return type.value() + " " + identifier.value() + " = " + expression + ";";
    }
    
}
