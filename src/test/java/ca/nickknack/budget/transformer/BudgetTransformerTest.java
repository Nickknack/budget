package ca.nickknack.budget.transformer;

import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.entity.Budget;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetTransformerTest {

    private static final Integer TEST_YEAR = 2020;
    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");

    @Test
    public void testToBudgetDtoFromValidEntity_shouldSetAllFields() {
        Budget input = getNewValidBudget();

        BudgetDto output = BudgetTransformer.toBudgetDto(input);

        assertAll(
                () -> assertEquals(input.getYear(), output.getYear(), "year"),
                () -> assertEquals(input.getExpectedTotal(), output.getExpectedTotal(), "expected total")
        );
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }
}
