package com.compiler.ast.statements.declaration;

import java.util.List;

import com.compiler.ast.Type;
import com.compiler.ast.expressions.DeclarativeExpression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.ast.statements.DeclarationStatement;

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
        var type = this.type.token();
        for (var identifier : identifiers) {
            identifier.validateType(type, parent);
        }
    }

    @Override
    public String toString() {
        return "";
        // if (expression == null) {
        //     return type.value() + " " + identifier.value() + ";";
        // }
        // return type.value() + " " + identifier.value() + " = " + expression + ";";
    }
    
}
