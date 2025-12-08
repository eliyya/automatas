import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

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
}
