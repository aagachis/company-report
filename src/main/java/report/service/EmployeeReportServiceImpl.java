package report.service;

import report.EmployeeCsvParser;
import report.exception.InvalidDataException;
import report.model.Employee;
import report.model.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static report.configuration.ParametersConfig.LESS_THAN_MULTIPLIER;
import static report.configuration.ParametersConfig.MAX_DEPTH_TO_CEO;
import static report.configuration.ParametersConfig.MORE_THAN_MULTIPLIER;

public class EmployeeReportServiceImpl implements EmployeeReportService {

    private static int ceoId;
    private final PrintReportService printReportService = new PrintReportServiceImpl();

    @Override
    public void generateCompanyReport(String filePath) {
        List<Employee> employeeList;
        try {
            // Load employee data from CSV file
            employeeList = EmployeeCsvParser.transformIntoEmployee(filePath);
        } catch (IOException | InvalidDataException e) {
            System.out.println("The file cannot be read: " + e.getMessage());
            return;
        }

        // Build the map containing managers as keys and their employees as values
        Map<Integer, List<Employee>> managerToEmployees = buildManagerToEmployeesMap(employeeList);

        // Calculate average salary for the direct subordinates of each manager
        Map<Integer, Double> managerToAverageSalary = calculateAverageSalaries(managerToEmployees);

        // Get the managers that are earning less than 20% more than average of direct subordinates
        List<Pair<Double>> managersEarningLess = findManagersByFilter(managerToEmployees, employeeList,
                employee -> employee.salary() < managerToAverageSalary.get(employee.id()) * LESS_THAN_MULTIPLIER,
                employee -> new Pair<>(employee.id(),
                        formatDoubleWithTwoDecimals(managerToAverageSalary.get(employee.id()) * LESS_THAN_MULTIPLIER - employee.salary())));

        // Get the managers that are earning more than 50% more than average of direct subordinates
        List<Pair<Double>> managersEarningMore = findManagersByFilter(managerToEmployees, employeeList,
                employee -> employee.salary() > managerToAverageSalary.get(employee.id()) * MORE_THAN_MULTIPLIER,
                employee -> new Pair<>(employee.id(),
                        formatDoubleWithTwoDecimals(employee.salary() - managerToAverageSalary.get(employee.id()) * MORE_THAN_MULTIPLIER)));

        // Get the employees that have more than 4 managers between them and the CEO
        List<Pair<Integer>> employeesWithLongerLine = findEmployeesWithLongerReportingLine(managerToEmployees, employeeList);

        // Print the expected results
        printReportService.printResults(managersEarningLess, "earns less than expected by");
        printReportService.printResults(managersEarningMore, "earns more than expected by");
        printReportService.printResults(employeesWithLongerLine, "has a reporting line longer than expected by");

    }

    /**
     * Create the map containing the managerIds as keys and the list of direct subordinates for each manager as values
     *
     * @param employeeList The list of employees
     */
    private static Map<Integer, List<Employee>> buildManagerToEmployeesMap(List<Employee> employeeList) {
        return employeeList.stream().filter(employee -> employee.managerId() != null)
                .collect(Collectors.groupingBy(Employee::managerId));
    }

    /**
     * Calculate the average salary of the direct subordinates for each manager
     *
     * @param managerToEmployees The map containing the managerIds as keys and the list of direct subordinates for each manager as values
     */
    private static Map<Integer, Double> calculateAverageSalaries(Map<Integer, List<Employee>> managerToEmployees) {
        Map<Integer, Double> managerToAverageSalary = new HashMap<>();
        managerToEmployees.forEach((key, value) -> managerToAverageSalary.put(key,
                formatDoubleWithTwoDecimals(value.stream().mapToDouble(Employee::salary).average().orElse(0))));
        return managerToAverageSalary;
    }

    /**
     * Filter the list of employees based on the provided filter and maps each employee to a Pair<Double> object using the given mapper
     * Only employees that are found in the managerToEmployee map are taken into consideration
     *
     * @param managerToEmployees The map containing the managerIds as keys and the list of direct subordinates for each manager as values
     * @param employeeList       The list of employees that should be filtered
     * @param filter             The predicate used to filter the employees
     * @param mapper             The function used to map each filtered employee
     * @return A list containing Pair objects that hold each managerId together with the salary difference
     */
    private static List<Pair<Double>> findManagersByFilter(Map<Integer, List<Employee>> managerToEmployees,
                                                           List<Employee> employeeList, Predicate<Employee> filter,
                                                           Function<Employee, Pair<Double>> mapper) {
        return employeeList.stream()
                .filter(employee -> managerToEmployees.containsKey(employee.id()) && filter.test(employee))
                .map(mapper)
                .toList();
    }

    /**
     * Get the list of employees that have more than 4 managers between them and the CEO and by how much
     *
     * @param managerToEmployees The map containing the managerIds as keys and the list of direct subordinates for each manager as values
     * @param employeeList       The list of employees
     * @return the list of employees that have more than 4 managers between them and the CEO and by how much
     */
    private static List<Pair<Integer>> findEmployeesWithLongerReportingLine(Map<Integer, List<Employee>> managerToEmployees,
                                                                            List<Employee> employeeList) {
        return employeeList.stream()
                .filter(employee -> calculateDepthToCeo(managerToEmployees, employee.id()) > MAX_DEPTH_TO_CEO)
                .map(employee -> new Pair<>(employee.id(), calculateDepthToCeo(managerToEmployees, employee.id()) - MAX_DEPTH_TO_CEO))
                .toList();
    }

    /**
     * Calculate how many managers does an employee have between them and the CEO
     *
     * @param managerToEmployees The map containing the managerIds as keys and the list of direct subordinates for each manager as values
     * @param employeeId         The id of the employee
     * @return The number of managers that an employee have between them and the CEO
     */
    private static int calculateDepthToCeo(Map<Integer, List<Employee>> managerToEmployees, int employeeId) {

        Integer managerId = managerToEmployees.entrySet().stream()
                .filter(entry -> entry.getValue().stream().map(Employee::id).toList().contains(employeeId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(() -> {
                    ceoId = employeeId;
                    return null;
                });

        // if the employee is the CEO, or the manager is the CEO, skip increasing depth
        return employeeId == ceoId || Objects.equals(managerId, ceoId) ? 0 : calculateDepthToCeo(managerToEmployees, managerId) + 1;
    }

    private static Double formatDoubleWithTwoDecimals(Double value) {
        return Math.round(value * 100d) / 100d;
    }
}
