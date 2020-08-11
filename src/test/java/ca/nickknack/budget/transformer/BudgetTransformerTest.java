package ca.nickknack.budget.transformer;

import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.dto.BudgetInputDto;
import ca.nickknack.budget.entity.Budget;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetTransformerTest {

    private static final Integer TEST_YEAR = 2020;
    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");

    private static final Integer NEW_TEST_YEAR = 2021;
    private static final BigDecimal NEW_TEST_EXPECTED_TOTAL = new BigDecimal("60000");

    @Test
    public void testToBudgetDtoFromValidEntity_shouldSetAllFields() {
        Budget input = getNewValidBudget();

        BudgetDto output = BudgetTransformer.toBudgetDto(input);

        assertAll(
                () -> assertEquals(input.getBudgetId(), output.getBudgetId(), "budget ID"),
                () -> assertEquals(input.getYear(), output.getYear(), "year"),
                () -> assertEquals(input.getExpectedTotal(), output.getExpectedTotal(), "expected total")
        );
    }

    @Test
    public void testToBudgetFromBudgetInputDto_shouldSetAllFieldsOnBudget() {
        Budget input = getNewValidBudget();
        UUID budgetId = input.getBudgetId();
        BudgetInputDto budgetInputDto = getNewValidBudgetInputDto();

        Budget result = BudgetTransformer.toBudget(input, budgetInputDto);

        assertAll(
                () -> assertEquals(budgetId, result.getBudgetId(), "budget ID"),
                () -> assertEquals(NEW_TEST_YEAR, result.getYear(), "year"),
                () -> assertEquals(NEW_TEST_EXPECTED_TOTAL, result.getExpectedTotal(), "expected total")
        );
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }

    private BudgetInputDto getNewValidBudgetInputDto() {
        return new BudgetInputDto()
                .setYear(NEW_TEST_YEAR)
                .setExpectedTotal(NEW_TEST_EXPECTED_TOTAL);
    }
}
