package report;

import report.exception.InvalidDataException;
import report.model.Employee;
import report.validator.InputValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static report.configuration.ParametersConfig.COMMA_DELIMITER;
import static report.configuration.ParametersConfig.MIN_NUMBER_OF_COLUMNS;

/**
 * Utility class for reading CSV files containing employee data
 */
public class EmployeeCsvParser {

    public static final String IN_LINE = " in line ";

    private EmployeeCsvParser() {
    }

    /**
     * Reads employee data from a CSV file and returns a list of Employee objects
     *
     * @param filePath The path to the CSV file
     * @return A list of Employee objects
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws InvalidDataException If the data format is invalid.
     */
    public static List<Employee> transformIntoEmployee(String filePath) throws IOException, InvalidDataException {
        BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
        return populateEmployees(fileReader);
    }

    /**
     * Create a list of Employee objects from the data read from the file.
     *
     * @param fileReader The BufferedReader used to read the file.
     * @return A list of Employee objects parsed from the file.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws InvalidDataException If the data format is invalid.
     */
    private static List<Employee> populateEmployees(BufferedReader fileReader) throws IOException, InvalidDataException {
        int indexLine = 0;
        List<Employee> employeeList = new ArrayList<>();
        String line;
        while ((line = fileReader.readLine()) != null) {
            String[] parts = line.split(COMMA_DELIMITER);
            if (indexLine++ != 0 && parts.length >= MIN_NUMBER_OF_COLUMNS) {
                try {
                    Employee e = createEmployeeFromFileEntry(parts);
                    employeeList.add(e);
                } catch (InvalidDataException e) {
                    throw new InvalidDataException(e.getMessage() + IN_LINE + indexLine);
                }
            }
        }
        return employeeList;
    }

    /**
     * Create an Employee object from a single entry read from the file.
     *
     * @param parts The array of String parts representing a single entry from the file.
     * @return An Employee object parsed from the file entry.
     * @throws InvalidDataException If the data format is invalid.
     */
    private static Employee createEmployeeFromFileEntry(String[] parts) throws InvalidDataException {
        Integer managerId = null;
        if (parts.length > MIN_NUMBER_OF_COLUMNS) {
            managerId = InputValidator.validateManagerId(parts[MIN_NUMBER_OF_COLUMNS]);
        }

        return new Employee(InputValidator.validateId(parts[0]), parts[1], parts[2],
                InputValidator.validateSalary(parts[3]), managerId);
    }
}
