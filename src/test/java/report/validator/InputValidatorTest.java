package report.validator;

import org.junit.jupiter.api.Test;
import report.exception.InvalidDataException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InputValidatorTest {

    @Test
    void testInvalidIdValue() {

        //setup
        String expected = "Invalid id format";
        //execute
        Exception ex = assertThrows(InvalidDataException.class, () -> InputValidator.validateId("abc"));

        //verify
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void testValidIdValue() {

        //setup
        String input = "123";
        int expected = 123;

        //execute
        int response = InputValidator.validateId(input);

        // verify
        assertEquals(expected, response);
    }

    @Test
    void testInvalidSalaryValue() {

        //setup
        String expected = "Invalid salary format";

        //execute
        Exception ex = assertThrows(InvalidDataException.class, () -> InputValidator.validateSalary("abc"));

        //verify
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void testValidSalaryValue() {

        //setup
        String input = "10000.0";
        double expected = 10000.0;

        //execute
        double response = InputValidator.validateSalary(input);

        // verify
        assertEquals(expected, response);
    }

    @Test
    void testInvalidManagerIdValue() {

        //setup
        String expected = "Invalid manager id format";

        //execute
        Exception ex = assertThrows(InvalidDataException.class, () -> InputValidator.validateManagerId("abc"));

        //verify
        assertEquals(expected, ex.getMessage());
    }

    @Test
    void testValidManagerIdValue() {

        //setup
        String input = "123";
        int expected = 123;

        //execute
        int response = InputValidator.validateManagerId(input);

        // verify
        assertEquals(expected, response);
    }
}
