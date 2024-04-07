package report;

import org.junit.jupiter.api.Test;
import report.model.Employee;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvUtilsTest {

    @Test
    void testReadFromCsvValidPath() {
        // setup
        String filePath = "src/test/resources/data.csv";

        // execute
        List<Employee> employeeList = CsvUtils.getEmployeesFromCsv(filePath);

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
    }

    @Test
    void testReadFromCsvValidPathButEmptyFile() {
        // setup
        String filePath = "src/test/resources/emptyData.csv";

        // execute
        List<Employee> employees = CsvUtils.getEmployeesFromCsv(filePath);

        // verify
        assertEquals(0, employees.size());
    }

    @Test
    void testReadFromCsvInvalidPath() {
        // setup
        String filePath = "invalid_path";

        // execute
        List<Employee> employeeList = CsvUtils.getEmployeesFromCsv(filePath);

        // verify
        assertNotNull(employeeList);
        assertTrue(employeeList.isEmpty());
    }
}
