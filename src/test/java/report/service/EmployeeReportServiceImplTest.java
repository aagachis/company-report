package report.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import report.MainApplication;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeReportServiceImplTest {

    private final PrintStream printStream = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private final String FILE_NAME = "src/test/resources/employeeData.csv";

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void cleanUp() throws IOException {
        System.setOut(printStream);
        Files.delete(Paths.get(FILE_NAME));
    }

    @ParameterizedTest
    @MethodSource("generateFileInputAndExpectedResult")
    void testAppDisplaysDataForAllConditions(String fileContent, String expected) throws IOException {

        // setup
        createFileWithContent(fileContent);

        // execute
        MainApplication.main(FILE_NAME);

        // verify
        assertEquals(expected, outputStreamCaptor.toString().trim());
    }

    private static Stream<Arguments> generateFileInputAndExpectedResult() {
        return Stream.of(
                // employees that earns less, earns more and have reporting line longer than expected
                Arguments.of("""
                                Id,firstName,lastName,salary,managerId
                                123,Joe,Doe,80000,
                                124,Martin,Chekov,45000,123
                                125,Bob,Ronstad,47000,123
                                300,Alice,Hasacat,50000,124
                                305,Brett,Hardleaf,34000,300
                                306,A,B,40000,305
                                307,C,D,40000,306
                                308,E,F,40000,307
                                309,K,J,40000,308
                                310,K,J,40000,305""",
                        """
                                Employee with id=124 earns less than expected by 15000.0
                                Employee with id=305 earns less than expected by 14000.0
                                Employee with id=306 earns less than expected by 8000.0
                                Employee with id=307 earns less than expected by 8000.0
                                Employee with id=308 earns less than expected by 8000.0
                                Employee with id=123 earns more than expected by 11000.0
                                Employee with id=308 has a reporting line longer than expected by 1
                                Employee with id=309 has a reporting line longer than expected by 2"""),
                // only employees that earns less than expected
                Arguments.of("""
                                Id,firstName,lastName,salary,managerId
                                123,Joe,Doe,60000,
                                124,Martin,Chekov,45000,123
                                125,Bob,Ronstad,47000,123
                                300,Alice,Hasacat,50000,124
                                305,Brett,Hardleaf,34000,300""",
                        """
                                Employee with id=124 earns less than expected by 15000.0"""),
                // only employees that earns more than expected
                Arguments.of("""
                                Id,firstName,lastName,salary,managerId
                                123,Joe,Doe,90000,
                                124,Martin,Chekov,60000,123
                                125,Bob,Ronstad,47000,123
                                300,Alice,Hasacat,50000,124
                                305,Brett,Hardleaf,34000,300""",
                        """
                                Employee with id=123 earns more than expected by 9750.0"""),
                // only employees that have reporting line longer than expected
                Arguments.of("""
                                Id,firstName,lastName,salary,managerId
                                123,Joe,Doe,87919.68,
                                124,Martin,Chekov,99532.8,123
                                125,Bob,Ronstad,47000,123
                                300,Alice,Hasacat,82944,124
                                305,Brett,Hardleaf,69120,300
                                306,A,B,57600,305
                                307,C,D,48000,306
                                308,E,F,40000,307""",
                        """
                                Employee with id=308 has a reporting line longer than expected by 1"""),
                // employees that are not matching any of criteria -> empty expected result
                Arguments.of("""
                        Id,firstName,lastName,salary,managerId
                        123,Joe,Doe,87919.68,
                        124,Martin,Chekov,99532.8,123
                        125,Bob,Ronstad,47000,123
                        300,Alice,Hasacat,82944,124
                        305,Brett,Hardleaf,69120,300
                        306,A,B,57600,305
                        307,C,D,48000,306""", "")
        );
    }

    private void createFileWithContent(String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
        writer.write(content);
        writer.close();
    }
}
