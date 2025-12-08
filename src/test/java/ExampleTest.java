
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.compiler.lexer.Lexer;
import com.compiler.parser.Parser;

public class ExampleTest {
    @Test
    void pruebaTest() throws IOException {
        var source = Files.readString(Path.of("src/test/inputs/test.java.txt"));
        var lexer = new Lexer(source);
        var tokens = lexer.tokenize();
        var ast = Parser.parse(tokens);
        ast.validate();
    }

    @Test
    void pruebaCounter() throws IOException {
        var source = Files.readString(Path.of("src/test/inputs/counter.java.txt"));
        var lexer = new Lexer(source);
        var tokens = lexer.tokenize();
        var ast = Parser.parse(tokens);
        ast.validate();
    }

    @Test
    void fallaSiTipoEsInvalido() throws IOException {
        testFailingCase("invalid_type.java.txt");
    }

    @Test
    void fallaSiHaySumaInvalida() throws IOException {
        testFailingCase("invalid_add.java.txt");
    }

    @Test
    void fallaSiVariableNoDeclarada() throws IOException {
        testFailingCase("not_declared.java.txt");
    }

    @Test
    void fallaSiVariableRedefinida() throws IOException {
        testFailingCase("redeclare.java.txt");
    }

    @Test
    void fallaSiFuncionInexistente() throws IOException {
        testFailingCase("inexistent_function.java.txt");
    }

    @Test
    void fallaSiParentesisNoCerrados() throws IOException {
        testFailingCase("unclosed_parents.java.txt");
    }

    @Test
    void fallaSiLiteralInvalido() throws IOException {
        testFailingCase("literal_fail.java.txt");
    }

    @Test
    void fallaSiRangoInvalido() throws IOException {
        testFailingCase("range_error.java.txt");
    }

    void testFailingCase(String filename) throws IOException {
        var source = Files.readString(Path.of("src/test/inputs/fails/", filename));
        var lexer = new Lexer(source);
        var tokens = lexer.tokenize();
        Assertions.assertThrows(Exception.class, () -> {
            Parser.parse(tokens).validate();
        });
    }
}
