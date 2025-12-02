package com.compiler.ast.expressions.primary;

import java.util.regex.Pattern;

import com.compiler.ast.expressions.PrimaryExpression;
import com.compiler.ast.statements.BlockStatement;
import com.compiler.errors.InvalidTypeError;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenKind;

public class NumberExpression implements PrimaryExpression {
    final String _c = "NumberExpression";
    Token value;

    public NumberExpression(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.value();
    }

    @Override
    public void validateType(Token type, BlockStatement parent) {
        if (type.kind() == TokenKind.FLOAT) {
            if (!this.isValidFloat()) {
                throw new InvalidTypeError(type, this.value);
            }
        } else if (type.kind() == TokenKind.BYTE) {
            if (!this.isValidByte()) {
                throw new InvalidTypeError(type, this.value);
            }
        } else if (type.kind() == TokenKind.SHORT) {
            if (!this.isValidShort()) {
                throw new InvalidTypeError(type, this.value);
            }
        } else if (type.kind() == TokenKind.LONG) {
            if (!this.isValidLong()) {
                throw new InvalidTypeError(type, this.value);
            }
        } else if (type.kind() == TokenKind.INT) {
            if (!this.isValidInt()) {
                throw new InvalidTypeError(type, this.value);
            }
        } else if (type.kind() == TokenKind.CHAR) {
            if (!this.isValidInt() || this.isValidShort() || this.isValidByte()) {
                throw new InvalidTypeError(type, this.value);
            }
        } else if (type.kind() == TokenKind.STRING) {
            throw new InvalidTypeError(type, this.value);
        } else if (type.kind() == TokenKind.VOID) {
            throw new InvalidTypeError(type, this.value);
        } else {
            throw new UnsupportedOperationException("Unimplemented method 'validateType' for type " + type.value());
        }
    }

    private boolean isValidFloat() {
        return (value.value().endsWith("f") || value.value().endsWith("F"));
    }

    private boolean isBinary() {
        return Pattern.compile("^[+-]?0[Bb][01]([01_]*[01])$").matcher(value.value()).matches();
    }

    private boolean isHex() {
        return Pattern.compile(
                "^[+-]?0[xX](([0-9a-fA-F][0-9a-fA-F_]*[0-9a-fA-F]+)|[0-9a-fA-F]+)(\\\\.(([0-9a-fA-F][0-9a-fA-F_]*[0-9a-fA-F]+)|[0-9a-fA-F]+)[Pp][+-]?[0-9]+)?([fFdDdLl])?$")
                .matcher(value.value()).matches();
    }

    private boolean isOctal() {
        if (this.isBinary() || this.isHex())
            return false;
        return Pattern.compile("^[+-]?0[0-7_]*[0-7]+[fFdDLl]?$").matcher(value.value()).matches();
    }

    private boolean isValidByte() {
        if (this.value.value().contains("."))
            return false;
        if (this.isBinary()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[bB]", ""), 2) <= Byte.MAX_VALUE;
        }
        if (this.isHex()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[xX]", ""), 16) <= Byte.MAX_VALUE;
        }
        if (this.isOctal()) {
            return Integer.parseInt(this.value.value(), 8) <= Byte.MAX_VALUE;
        }
        return Integer.parseInt(this.value.value()) <= Byte.MAX_VALUE;
    }

    private boolean isValidShort() {
        if (this.value.value().contains("."))
            return false;
        if (this.isBinary()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[bB]", ""), 2) <= Short.MAX_VALUE;
        }
        if (this.isHex()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[xX]", ""), 16) <= Short.MAX_VALUE;
        }
        if (this.isOctal()) {
            return Integer.parseInt(this.value.value(), 8) <= Short.MAX_VALUE;
        }
        return Integer.parseInt(this.value.value()) <= Short.MAX_VALUE;
    }

    private boolean isValidLong() {
        if (this.value.value().contains("."))
            return false;
        if (this.isBinary()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[bB]", ""), 2) <= Long.MAX_VALUE;
        }
        if (this.isHex()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[xX]", ""), 16) <= Long.MAX_VALUE;
        }
        if (this.isOctal()) {
            return Integer.parseInt(this.value.value(), 8) <= Long.MAX_VALUE;
        }
        try {
            return Long.parseLong(this.value.value()) > Integer.MAX_VALUE;
        } catch (Exception e) {
            return Long.parseLong(this.value.value().replace("l", "").replace("L", "")) <= Long.MAX_VALUE;
        }
    }

    private boolean isValidInt() {
        if (this.value.value().contains("."))
            return false;
        if (this.isBinary()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[bB]", ""), 2) <= Long.MAX_VALUE;
        }
        if (this.isHex()) {
            return Integer.parseInt(this.value.value().replaceFirst("0[xX]", ""), 16) <= Long.MAX_VALUE;
        }
        if (this.isOctal()) {
            return Integer.parseInt(this.value.value(), 8) <= Long.MAX_VALUE;
        }
        if (this.isValidLong()) return false;
        return Integer.parseInt(this.value.value()) <= Integer.MAX_VALUE;
    }

}
