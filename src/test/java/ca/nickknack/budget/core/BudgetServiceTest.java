package ca.nickknack.budget.core;

import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.exception.NotFoundException;
import ca.nickknack.budget.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;
    @MockBean
    private BudgetRepository mockBudgetRepository;

    private static final Integer TEST_YEAR = 2020;
    private static final Integer TEST_YEAR_2 = 2021;
    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");

    private static final Integer INVALID_YEAR = 1000;

    @BeforeEach
    public void setupMocks() {
        when(mockBudgetRepository.findBudgetByYear(TEST_YEAR)).thenReturn(getNewValidBudget());
        when(mockBudgetRepository.findBudgetByYear(INVALID_YEAR)).thenReturn(null);
    }

    @Test
    public void testGetBudgetWithValidYear_shouldReturnBudget() {
        Budget result = budgetService.getBudget(TEST_YEAR);

        assertNotNull(result);

        assertAll(
                () -> assertEquals(TEST_YEAR, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testGetBudgetWithInvalidYear_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> budgetService.getBudget(INVALID_YEAR));
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }

    @Test
    public void testDeleteBudgetWithValidYear_shouldCallDeleteMethodWithCorrectBudget() {
        Budget testBudget = getNewValidBudget().setYear(TEST_YEAR_2);

        when(mockBudgetRepository.findBudgetByYear(TEST_YEAR_2)).thenReturn(testBudget);

        budgetService.deleteBudget(TEST_YEAR_2);

        verify(mockBudgetRepository, times(1)).delete(testBudget);
    }

    @Test
    public void testDeleteBudgetWithinValidYear_shouldNotCallDelete() {
        budgetService.deleteBudget(INVALID_YEAR);

        verify(mockBudgetRepository, times(0)).delete(any());
    }
}
