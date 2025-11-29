package com.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import com.compiler.ast.Expression;
import com.compiler.ast.Statment;
import com.compiler.ast.expressions.AssignmentExpression;
import com.compiler.ast.expressions.IdentifierExpression;
import com.compiler.ast.expressions.UnaryOperationExpression;
import com.compiler.ast.statments.BlockStatment;
import com.compiler.ast.statments.ContolFlowStatment;
import com.compiler.ast.statments.DeclarationStatment;
import com.compiler.ast.statments.ExpressionStatment;
import com.compiler.ast.statments.FunctionStatment;
import com.compiler.ast.statments.ParameterStatment;
import com.compiler.ast.statments.control_flow.DoWhileStatment;
import com.compiler.ast.statments.control_flow.ForStatment;
import com.compiler.ast.statments.control_flow.IfStatment;
import com.compiler.ast.statments.control_flow.WhileStatment;
import com.compiler.errors.ExpectedError;
import com.compiler.lexer.TokenKind;

public class StatmentParser {
    public static BlockStatment parseBlockStatment(Parser parser) {
        return parseBlockStatment(true, parser);
    }

    public static BlockStatment parseBlockStatment(boolean inner, Parser parser) throws ExpectedError {
        var body = new ArrayList<Statment>();

        while (parser.hasTokens()) {
            var handler = PrattRegistry.stmtLU.get(parser.currentTokenKind());
            if (handler == null) {
                throw new ExpectedError(parser, "statment", parser.currentToken());
            }
            body.add(handler.handle(parser));
            continue;

            // if (inner && parser.currentTokenKind() == TokenKind.CLOSE_CURLY)
            //     break;
            // var currentToken = parser.currentToken();
            // // start with type
            // if (TokenKind.isPrimitiveType(currentToken)) {
            //     if (parser.getToken(2).kind() == TokenKind.OPEN_PAREN) {
            //         body.add(parseFunctionStatment(parser));
            //     }
            //     // start with identifier
            // } else if (currentToken.kind() == TokenKind.IDENTIFIER) {
            //     var nextToken = parser.nextToken();
            //     if (TokenKind.isAssignment(nextToken.kind())) {
            //         // continue with assignment
            //         body.add(parseAssignmentStatment(parser));
            //     } else if (TokenKind.isUnaryOperation(nextToken.kind())) {
            //         // continue with unary operation
            //         body.add(parseUnaryOperation(parser));
            //     } else if (nextToken.kind() == TokenKind.OPEN_PAREN) {
            //         // continue with function call
            //         body.add(parseFunctionCallStatment(parser));
            //     } else {
            //         var expression = parseExpression(parser);
            //         var semi = parser.advance();
            //         if (semi.kind() != TokenKind.SEMI) {
            //             throw new ExpectedError(parser, ";", semi);
            //         }
            //         body.add(new ExpressionStatment(expression));
            //     }
            //     // start with unary operation
            // } else if (TokenKind.isUnaryOperation(currentToken.kind())) {
            //     body.add(parseUnaryOperation(parser));
            //     // start with control flow
            // } else if (TokenKind.isControlFlow(currentToken.kind())) {
            //     body.add(parseControlFlowStatment(parser));
            // } else if (currentToken.kind() == TokenKind.VAR) {
            //     body.add(parseTypedVarStatment(parser));
            // } else if (currentToken.kind() == TokenKind.OPEN_CURLY) {
            //     parser.expect(TokenKind.OPEN_CURLY);
            //     body.add(parseBlockStatment(parser));
            //     parser.expect(TokenKind.CLOSE_CURLY);
            // } else {
            //     throw new ExpectedError(parser, "statment", currentToken);
            // }
        }

        return new BlockStatment(body);
    }

    // -----------------------
    // parse function statment
    // -----------------------
    private static FunctionStatment parseFunctionStatment(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type)) {
            throw new ExpectedError(parser, "type", type);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        parser.expect(TokenKind.OPEN_PAREN);
        var parameters = new ArrayList<ParameterStatment>();
        while (parser.currentTokenKind() != TokenKind.CLOSE_PAREN) {
            parameters.add(parseParameterStatment(parser));
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                parser.advance();
                continue;
            }
            break;
        }
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parseBlockStatment(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new FunctionStatment(type, identifier, parameters, body);
    }

    private static ExpressionStatment parseFunctionCallStatment(Parser parser) throws ExpectedError {
        var functionCall = ExpressionParser.parseFunctionExpression(parser);
        parser.expect(TokenKind.SEMI);
        return new ExpressionStatment(functionCall);
    }

    private static ParameterStatment parseParameterStatment(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type)) {
            throw new ExpectedError(parser, "type", type);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        return new ParameterStatment(type, identifier);
    }

    // -----------------------
    // parse variable statment
    // -----------------------
    public static DeclarationStatment parseTypedVarStatment(Parser parser) throws ExpectedError {
        var type = parser.advance();
        if (!TokenKind.isPrimitiveType(type)) {
            throw new ExpectedError(parser, "type", type);
        }
        var expressions = new ArrayList<Expression>();
        while (true) {
            var identifier = parser.expect(TokenKind.IDENTIFIER);
            if (parser.currentTokenKind() == TokenKind.ASSIGNMENT) {
                var assignment = parser.advance();
                var expression = ExpressionParser.parseExpression(parser);
                expressions.add(new AssignmentExpression(identifier, assignment, expression));
            }
            // ends with ,
            if (parser.currentTokenKind() == TokenKind.COMMA) {
                expressions.add(new IdentifierExpression(identifier));
                parser.advance();
                continue;
            }
            // ends with ;
            if (parser.currentTokenKind() == TokenKind.SEMI) {
                break;
            }
            throw new ExpectedError(parser,";", parser.currentToken());
        }
        parser.expect(TokenKind.SEMI);
        return new DeclarationStatment(type, expressions);
    }

    private static DeclarationStatment parseUniqueVariableStatment(Parser parser) throws ExpectedError {
        var tokenKind = parser.advance();
        if (!TokenKind.isPrimitiveType(tokenKind)) {
            throw new ExpectedError(parser, "type", tokenKind);
        }
        var identifier = parser.expect(TokenKind.IDENTIFIER);
        return new DeclarationStatment(tokenKind, List.of(new IdentifierExpression(identifier)));
    }

    // ----------------
    // parse assignment
    // ----------------
    private static ExpressionStatment parseAssignmentStatment(Parser parser) throws ExpectedError {
        var expression = parseAssignment(parser);
        parser.expect(TokenKind.SEMI);
        return new ExpressionStatment(expression);
    }

    private static AssignmentExpression parseAssignment(Parser parser) throws ExpectedError {
        var identifier = parser.advance();
        if (identifier.kind() != TokenKind.IDENTIFIER) {
            throw new ExpectedError(parser, "identifier", identifier);
        }
        var equal = parser.advance();
        if (!TokenKind.isAssignment(equal)) {
            throw new ExpectedError(parser, "=", equal);
        }
        var expression = ExpressionParser.parseExpression(parser);
        return new AssignmentExpression(identifier, equal, expression);
    }

    // ---------------------
    // parse unary operation
    // ---------------------
    private static ExpressionStatment parseUnaryOperation(Parser parser) throws ExpectedError {
        var first = parser.currentToken();
        if (TokenKind.isUnaryOperation(first)) {
            return parsePrefixOperation(parser);
        } else {
            return parsePostfixOperation(parser);
        }
    }

    private static ExpressionStatment parsePostfixOperation(Parser parser) throws ExpectedError {
        var first = parser.expect(TokenKind.IDENTIFIER);
        var operation = parser.advance();
        if (!TokenKind.isUnaryOperation(operation)) {
            throw new ExpectedError(parser, "postfix operation", operation);
        }
        parser.expect(TokenKind.SEMI);
        return new ExpressionStatment(new UnaryOperationExpression(first, operation, false));
    }

    private static ExpressionStatment parsePrefixOperation(Parser parser) throws ExpectedError {
        var operation = parser.advance();
        if (!TokenKind.isUnaryOperation(operation)) {
            throw new ExpectedError(parser, "prefix operation", operation);
        }
        var identifier = parser.advance();
        if (identifier.kind() != TokenKind.IDENTIFIER) {
            throw new ExpectedError(parser, "identifier", identifier);
        }
        var semi = parser.advance();
        if (semi.kind() != TokenKind.SEMI) {
            throw new ExpectedError(parser, ";", semi);
        }
        return new ExpressionStatment(new UnaryOperationExpression(identifier, operation, true));
    }

    // private static Statment parseFunctionStatment(Parser parser) throws
    // ExpectedError {
    // var identifier = parser.advance();
    // if (identifier.kind() != TokenKind.IDENTIFIER) {
    // throw new ExpectedError(parser, "identifier", identifier);
    // }
    // var openParent = parser.advance();
    // if (openParent.kind() != TokenKind.OPEN_PAREN) {
    // throw new ExpectedError(parser, "(", openParent);
    // }
    // var closeParent = parser.advance();
    // if (closeParent.kind() != TokenKind.CLOSE_PAREN) {
    // throw new ExpectedError(parser, ")", closeParent);
    // }
    // return new ExpressionStatment(new FunctionExpression(identifier));
    // }

    // ------------------
    // parse control flow
    // ------------------
    private static ContolFlowStatment parseControlFlowStatment(Parser parser) {
        var token = parser.currentToken();
        switch (token.kind()) {
            case WHILE -> {
                return parseWhileStatment(parser);
            }
            case IF -> {
                return parseIfStatment(parser);
            }
            case ELSE -> {
                parser.expect(TokenKind.IF);
                return null;
            }
            case FOR -> {
                return parseForStatment(parser);
            }
            case DO -> {
                return parseDoStatment(parser);
            }
            default -> {
                throw new UnsupportedOperationException("Unimplemented method 'parseControlFlowStatment'");
            }
        }
    }

    private static WhileStatment parseWhileStatment(Parser parser) {
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = ExpressionParser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parseBlockStatment(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new WhileStatment(expression, body);
    }

    private static IfStatment parseIfStatment(Parser parser) {
        // if
        parser.expect(TokenKind.IF);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = ExpressionParser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parseBlockStatment(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        if (parser.currentTokenKind() != TokenKind.ELSE) {
            return new IfStatment(expression, body);
        }
        // else
        parser.advance();
        // if again
        if (parser.currentTokenKind() == TokenKind.IF) {
            var elseIfBody = parseIfStatment(parser);
            var elseBody = new BlockStatment(List.of(elseIfBody));
            return new IfStatment(expression, body, elseBody);
        }
        parser.expect(TokenKind.OPEN_CURLY);
        var elseBody = parseBlockStatment(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new IfStatment(expression, body, elseBody);
    }

    private static ForStatment parseForStatment(Parser parser) {
        parser.expect(TokenKind.FOR);
        parser.expect(TokenKind.OPEN_PAREN);
        // foreach
        if (parser.getToken(2).kind() == TokenKind.COLON) {
            var stat = parseUniqueVariableStatment(parser);
            parser.expect(TokenKind.COLON);
            var collection = parser.expect(TokenKind.IDENTIFIER);
            parser.expect(TokenKind.CLOSE_PAREN);
            parser.expect(TokenKind.OPEN_CURLY);
            var body = parseBlockStatment(parser);
            parser.expect(TokenKind.CLOSE_CURLY);
            return new ForStatment(stat, collection, body);
        }
        // for
        var stat = parseTypedVarStatment(parser);
        var condition = ExpressionParser.parseExpression(parser);
        parser.expect(TokenKind.SEMI);
        var increment = ExpressionParser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parseBlockStatment(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        return new ForStatment(stat, condition, increment, body);
    }

    private static DoWhileStatment parseDoStatment(Parser parser) {
        parser.expect(TokenKind.DO);
        parser.expect(TokenKind.OPEN_CURLY);
        var body = parseBlockStatment(parser);
        parser.expect(TokenKind.CLOSE_CURLY);
        parser.expect(TokenKind.WHILE);
        parser.expect(TokenKind.OPEN_PAREN);
        var expression = ExpressionParser.parseExpression(parser);
        parser.expect(TokenKind.CLOSE_PAREN);
        parser.expect(TokenKind.SEMI);
        return new DoWhileStatment(body, expression);
    }
}
