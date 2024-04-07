package report;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainApplicationTest {

    private final PrintStream printStream = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void cleanUp() {
        System.setOut(printStream);
    }

    @Test
    void testAppDisplaysCorrectInfo() {
        // setup
        String expectedOutput = """
                Employee with id=124 earns less than expected by 15000.0
                Employee with id=305 earns less than expected by 14000.0
                Employee with id=306 earns less than expected by 8000.0
                Employee with id=307 earns less than expected by 8000.0
                Employee with id=308 earns less than expected by 8000.0
                Employee with id=123 earns more than expected by 11000.0
                Employee with id=308 has a reporting line longer than expected by 1
                Employee with id=309 has a reporting line longer than expected by 2""";

        // execute
        MainApplication.main("src/test/resources/fullData.csv");

        // verify
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }
}
