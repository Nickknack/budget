package ca.nickknack.budget.core;

import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.repository.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class BudgetServiceTest {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private BudgetRepository budgetRepository;

    private static final Integer TEST_YEAR = 2020;
    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");

    @Test
    public void testGetBudgetByYearSingleBudget_ShouldReturnValidBudget() {
        Budget testBudget1 = getNewValidBudget();
        budgetRepository.save(testBudget1);

        Budget result = budgetService.findBudget(TEST_YEAR);
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);

    }
}
