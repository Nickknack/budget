package ca.nickknack.budget.core;

import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.exception.NotFoundException;
import ca.nickknack.budget.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class BudgetServiceTest {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private BudgetRepository budgetRepository;

    private static final Integer TEST_YEAR = 2020;
    private static final Integer INVALID_YEAR = 1000;
    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");

    @BeforeEach
    public void clearData() {
        budgetRepository.deleteAll();
    }

    @Test
    public void testGetBudgetByYearSingleBudget_ShouldReturnValidBudget() {
        Budget testBudget1 = getNewValidBudget();
        budgetRepository.save(testBudget1);

        Budget result = budgetService.getBudget(TEST_YEAR);

        assertNotNull(result);

        assertAll(
                () -> assertEquals(TEST_YEAR, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testGetBudgetByInvalidYear_ShouldThrowNotFoundException() {
        Budget testBudget1 = getNewValidBudget();
        budgetRepository.save(testBudget1);

        assertThrows(NotFoundException.class, () -> budgetService.getBudget(INVALID_YEAR));
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);

    }
}
