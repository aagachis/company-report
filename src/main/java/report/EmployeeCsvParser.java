package report;

import report.model.Employee;

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

    private EmployeeCsvParser() {
    }

    /**
     * Reads employee data from a CSV file ignoring the first line (header) and returns a list of Employee objects
     *
     * @param filePath The path to the CSV file
     * @return A list of Employee objects
     */
    public static List<Employee> parseCsvIntoEmployeeList(String filePath) {
        List<Employee> employeeList = new ArrayList<>();
        int indexLine = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            employeeList = createEmployeeList(fileReader, indexLine);
        } catch (IOException e) {
            System.out.println("The file cannot be read: " + e.getLocalizedMessage());
        }

        return employeeList;
    }

    /**
     * Create a list of Employee objects from the data read from the file.
     *
     * @param fileReader The BufferedReader used to read the file.
     * @param indexLine  The index of the current line being read.
     * @return A list of Employee objects parsed from the file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private static List<Employee> createEmployeeList(BufferedReader fileReader, int indexLine) throws IOException {
        List<Employee> employeeList = new ArrayList<>();
        String line;
        while ((line = fileReader.readLine()) != null) {
            String[] parts = line.split(COMMA_DELIMITER);
            if (indexLine++ != 0 && parts.length >= MIN_NUMBER_OF_COLUMNS) {
                try {
                    Employee e = createEmployeeFromFileEntry(parts);
                    employeeList.add(e);
                } catch (InvalidDataException e) {
                    System.out.println(e.getMessage() + indexLine);
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
        int id;
        try {
            id = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Invalid id format in line ");
        }

        double salary;
        try {
            salary = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException("Invalid salary format in line ");
        }

        Integer managerId = null;
        if (parts.length > MIN_NUMBER_OF_COLUMNS) {
            try {
                managerId = Integer.parseInt(parts[MIN_NUMBER_OF_COLUMNS]);
            } catch (NumberFormatException e) {
                throw new InvalidDataException("Invalid manager id format in line ");
            }
        }

        return new Employee(id, parts[1], parts[2], salary, managerId);
    }

    static class InvalidDataException extends Exception {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}
