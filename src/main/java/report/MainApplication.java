package report;

import report.model.Employee;
import report.model.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class MainApplication {

    private static final String FILE_NAME = "src/main/resources/data.csv";

    private static final double LESS_THAN_MULTIPLIER = 1.2;
    private static final double MORE_THAN_MULTIPLIER = 1.5;
    private static final Map<Integer, List<Employee>> managerToEmployees = new HashMap<>();
    private static final Map<Integer, Double> managerToAverageSalary = new HashMap<>();
    private static final Map<Integer, Integer> employeeToDepth = new HashMap<>();

    private static Integer ceoId;

    public static void main(String... args) {

        // Load employee data from CSV file
        List<Employee> employeeList = CsvUtils.getEmployeesFromCsv(args != null && args.length > 0 ? args[0] : FILE_NAME);

        // Build the map containing managers as keys and their employees as values
        buildManagerToEmployeesMap(employeeList);

        // Calculate average salary for the direct subordinates of each manager
        calculateAverageSalaries();

        // Create the predicate for filtering the managers that are earning less than 20% more than average of direct subordinates
        Predicate<Employee> lessThanPredicate = employee -> employee.salary() < managerToAverageSalary.get(employee.id()) * LESS_THAN_MULTIPLIER;

        // Create the mapper containing the managers that are earning less than 20% more than average of direct subordinates and by how much
        Function<Employee, Pair<Double>> lessThanMapper = employee -> new Pair<>(employee.id(), managerToAverageSalary.get(employee.id()) * LESS_THAN_MULTIPLIER - employee.salary());

        // Get the managers that are earning less than 20% more than average of direct subordinates
        List<Pair<Double>> managersEarningLess = findManagersByFilter(employeeList, lessThanPredicate, lessThanMapper);

        // Create the predicate for filtering the managers that are earning more than 50% more than average of direct subordinates
        Predicate<Employee> moreThanPredicate = employee -> employee.salary() > managerToAverageSalary.get(employee.id()) * MORE_THAN_MULTIPLIER;

        // Create the mapper containing the managers that are earning more than 50% more than average of direct subordinates and by how much
        Function<Employee, Pair<Double>> moreThanMapping = employee -> new Pair<>(employee.id(), employee.salary() - managerToAverageSalary.get(employee.id()) * MORE_THAN_MULTIPLIER);

        // Get the managers that are earning more than 50% more than average of direct subordinates
        List<Pair<Double>> managersEarningMore = findManagersByFilter(employeeList, moreThanPredicate, moreThanMapping);

        // Get the employees that have more than 4 managers between them and the CEO
        List<Pair<Integer>> employeesWithLongerLine = findEmployeesWithLongerLine(employeeList);

        // Print the expected results
        printResults(managersEarningLess, "earns less than expected by");
        printResults(managersEarningMore, "earns more than expected by");
        printResults(employeesWithLongerLine, "has a reporting line longer than expected by");
    }

    /**
     * Create the map containing the managerIds as keys and the list of direct subordinates for each manager as values
     * @param employeeList The list of employees
     */
    private static void buildManagerToEmployeesMap(List<Employee> employeeList) {
        employeeList.stream().filter(employee -> employee.managerId() != null)
                .forEach(employee ->
                        managerToEmployees.computeIfAbsent(employee.managerId(), value -> new ArrayList<>()).add(employee));
    }

    /**
     * Calculate the average salary of the direct subordinates for each manager
     */
    private static void calculateAverageSalaries() {
        managerToEmployees.forEach((key, value) -> managerToAverageSalary.put(key, value.stream().mapToDouble(Employee::salary).sum() / value.size()));
    }

    /**
     * Filter the list of employees based on the provided filter and maps each employee to a Pair<Double> object using the given mapper
     * Only employees that are found in the managerToEmployee map are taken into consideration
     * @param employeeList The list of employees that should be filtered
     * @param filter The predicate used to filter the employees
     * @param mapper The function used to map each filtered employee
     * @return A list containing Pair objects that hold each managerId together with the salary difference
     */
    private static List<Pair<Double>> findManagersByFilter(List<Employee> employeeList, Predicate<Employee> filter,
                                                           Function<Employee, Pair<Double>> mapper) {
        return employeeList.stream()
                .filter(employee -> managerToEmployees.containsKey(employee.id()))
                .filter(filter)
                .map(mapper)
                .toList();
    }

    /**
     * Get the list of employees that have more than 4 managers between them and the CEO and by how much
     * @param employeeList The list of employees
     * @return the list of employees that have more than 4 managers between them and the CEO and by how much
     */
    private static List<Pair<Integer>> findEmployeesWithLongerLine(List<Employee> employeeList) {
        return employeeList.stream()
                .filter(employee -> calculateDepthToCeo(employee.id()) > 4)
                .map(employee -> new Pair<>(employee.id(), calculateDepthToCeo(employee.id())- 4))
                .toList();
    }

    /**
     * Calculate how many managers does an employee have between them and the CEO
     * @param employeeId The id of the employee
     * @return The number of managers that an employee have between them and the CEO
     */
    private static int calculateDepthToCeo(int employeeId) {
        if (employeeToDepth.containsKey(employeeId)) {
            return employeeToDepth.get(employeeId);
        }
        int managerId = managerToEmployees.entrySet().stream()
                .filter(entry -> entry.getValue().stream().map(Employee::id).toList().contains(employeeId))
                .map(Map.Entry::getKey)
                .findFirst().orElse(0);
        if (managerId == 0) {
            ceoId = employeeId;
        }

        // if the employee is the CEO, or the manager is the CEO, skip increasing depth
        int depth = managerId == 0 || managerId == ceoId ? 0 : calculateDepthToCeo(managerId) + 1;
        employeeToDepth.put(employeeId, depth);
        return depth;
    }

    private static <T> void printResults(List<Pair<T>> pairs, String message) {
        pairs.forEach(pair ->
                System.out.println("Employee with id=" + pair.id() + " " + message + " " + pair.diff()));
    }
}