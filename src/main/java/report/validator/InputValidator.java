package report.validator;

import report.exception.InvalidDataException;

public class InputValidator {

    public static final String INVALID_MANAGER_ID_FORMAT = "Invalid manager id format";
    public static final String INVALID_SALARY_FORMAT = "Invalid salary format";
    public static final String INVALID_ID_FORMAT = "Invalid id format";

    private InputValidator() {
    }

    public static int validateId(String value) {
        int intValue;
        try {
            intValue = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException(INVALID_ID_FORMAT);
        }
        return intValue;
    }

    public static double validateSalary(String value) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidDataException(INVALID_SALARY_FORMAT);
        }
        return doubleValue;
    }

    public static Integer validateManagerId(String value) {
        int integerValue;
        try {
            integerValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidDataException(INVALID_MANAGER_ID_FORMAT);
        }
        return integerValue;
    }
}
