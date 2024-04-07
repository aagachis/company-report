package report;

import report.model.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading CSV files containing employee data
 */
public class CsvUtils {

    private CsvUtils() {}

    private static final String COMMA_DELIMITER = ",";

    /**
     * Reads employee data from a CSV file ignoring the first line (header) and returns a list of Employee objects
     * @param filePath The path to the CSV file
     * @return A list of Employee objects
     */
    public static List<Employee> getEmployeesFromCsv(String filePath) {
        List<Employee> employeeList = new ArrayList<>();
        int indexLine = 0;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] parts = line.split(COMMA_DELIMITER);
                if (indexLine++ != 0 && parts.length >= 4) {
                    Employee employee = new Employee(Integer.parseInt(parts[0]), parts[1], parts[2], Long.valueOf(parts[3]), parts.length == 4 ? null : Integer.parseInt(parts[4]));
                    employeeList.add(employee);
                }
            }
        } catch (IOException e) {
            System.out.println("The file cannot be read: " + e.getLocalizedMessage());
        }

        return employeeList;
    }
}
