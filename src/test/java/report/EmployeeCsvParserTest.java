package report;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import report.exception.InvalidDataException;
import report.model.Employee;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeCsvParserTest {

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
    void testReadFromCsvValidPath() throws IOException {
        // setup
        String filePath = "src/test/resources/data.csv";

        // execute
        List<Employee> employeeList = EmployeeCsvParser.transformIntoEmployee(filePath);

        // verify
        assertNotNull(employeeList);
        assertFalse(employeeList.isEmpty());
        assertEquals(3, employeeList.size());
        assertEquals(123, employeeList.get(0).id());
        assertEquals(124, employeeList.get(1).id());
        assertEquals(125, employeeList.get(2).id());
        assertEquals("Joe", employeeList.get(0).firstName());
        assertEquals("Martin", employeeList.get(1).firstName());
        assertEquals("Bob", employeeList.get(2).firstName());
        assertEquals("Doe", employeeList.get(0).lastName());
        assertEquals("Chekov", employeeList.get(1).lastName());
        assertEquals("Ronstad", employeeList.get(2).lastName());
        assertEquals(80000, employeeList.get(0).salary());
        assertEquals(45000, employeeList.get(1).salary());
        assertEquals(47000, employeeList.get(2).salary());
        assertNull(employeeList.get(0).managerId());
        assertEquals(123, employeeList.get(1).managerId());
        assertEquals(123, employeeList.get(2).managerId());
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    void testReadFromCsvValidPathButInvalidData() {
        // setup
        String filePath = "src/test/resources/wrongFormatData.csv";
        String expectedMessage = "Invalid id format in line 3";

        // execute
        Exception exception = assertThrows(InvalidDataException.class, () -> EmployeeCsvParser.transformIntoEmployee(filePath));

        // verify
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testReadFromCsvValidPathButEmptyFile() throws IOException {
        // setup
        String filePath = "src/test/resources/emptyData.csv";

        // execute
        List<Employee> employees = EmployeeCsvParser.transformIntoEmployee(filePath);

        // verify
        assertEquals(0, employees.size());
        assertEquals("", outputStreamCaptor.toString().trim());
    }

    @Test
    void testReadFromCsvInvalidPath() {
        // setup
        String filePath = "invalid_path";
        String expectedMessage = "invalid_path (No such file or directory)";

        // execute
        Exception exception = assertThrows(FileNotFoundException.class, () -> EmployeeCsvParser.transformIntoEmployee(filePath));

        // verify
        assertEquals(expectedMessage, exception.getMessage());
    }
}
