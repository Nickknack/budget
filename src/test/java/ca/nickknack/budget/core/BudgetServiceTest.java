package ca.nickknack.budget.core;

import ca.nickknack.budget.dto.BudgetInputDto;
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
import java.util.Optional;
import java.util.UUID;

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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;
    @MockBean
    private BudgetRepository mockBudgetRepository;

    private static final Integer TEST_YEAR = 2020;
    private static final Integer NEW_TEST_YEAR = 2021;

    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");
    private static final BigDecimal NEW_EXPECTED_TOTAL = new BigDecimal("60000");

    private static final UUID INVALID_ID = UUID.fromString("b543fd8f-8171-4a52-8399-f38a1a2af145");

    private UUID testBudgetId;

    @BeforeEach
    public void setupMocks() {
        Budget testBudget = getNewValidBudget();
        testBudgetId = testBudget.getBudgetId();

        when(mockBudgetRepository.findById(testBudgetId)).thenReturn(Optional.of(testBudget));
        when(mockBudgetRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
    }

    @Test
    public void testGetBudgetWithId_shouldReturnCorrespondingBudget() {
        Budget result = budgetService.getBudget(testBudgetId);

        assertNotNull(result);

        assertAll(
                () -> assertEquals(testBudgetId, result.getBudgetId(), "budget ID"),
                () -> assertEquals(TEST_YEAR, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testGetBudgetWithInvalidYear_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> budgetService.getBudget(INVALID_ID));
    }

    @Test
    public void testUpdateBudgetWithInvalidId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> budgetService.updateBudget(INVALID_ID, getNewValidBudgetInputDto()));
    }

    @Test
    public void testUpdateBudgetWithNewYearNoExistingBudgetWithYear_shouldCallSave() {
        BudgetInputDto budgetInputDto = getNewValidBudgetInputDto().setYear(NEW_TEST_YEAR);

        budgetService.updateBudget(testBudgetId, budgetInputDto);

        verify(mockBudgetRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateBudgetWithNewYearExistingBudgetWithYear_shouldThrowIllegalArgumentException() {
        when(mockBudgetRepository.existsByYear(NEW_TEST_YEAR)).thenReturn(Boolean.TRUE);

        BudgetInputDto budgetInputDto = getNewValidBudgetInputDto().setYear(NEW_TEST_YEAR);

        assertThrows(IllegalArgumentException.class, () -> budgetService.updateBudget(testBudgetId, budgetInputDto));
    }

    @Test
    public void testCreateBudgetWithExistingYear_shouldThrowIllegalArgumentException() {
        when(mockBudgetRepository.existsByYear(TEST_YEAR)).thenReturn(Boolean.TRUE);

        BudgetInputDto budgetInputDto = getNewValidBudgetInputDto();

        assertThrows(IllegalArgumentException.class, () -> budgetService.createBudget(budgetInputDto));
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }

    private BudgetInputDto getNewValidBudgetInputDto() {
        return new BudgetInputDto()
                .setYear(TEST_YEAR)
                .setExpectedTotal(NEW_EXPECTED_TOTAL);
    }
}
