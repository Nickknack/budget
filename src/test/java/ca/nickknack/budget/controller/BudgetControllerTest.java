package ca.nickknack.budget.controller;

import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BudgetControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    private static final Integer TEST_YEAR = 2020;
    private static final Integer TEST_YEAR_2 = 2021;
    private static final Integer TEST_YEAR_3 = 2022;
    private static final Integer INVALID_YEAR = 1000;

    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");
    private static final BigDecimal NEW_EXPECTED_TOTAL = new BigDecimal("60000");

    @BeforeEach
    public void clearData() {
        budgetRepository.deleteAll();
    }

    @Test
    public void testFindBudgetByYear_shouldReturnExpectedBudget() {
        Budget testBudget = getNewValidBudget();
        budgetRepository.save(testBudget);

        ResponseEntity<BudgetDto> response = getBudgetByYearRestCall(TEST_YEAR);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BudgetDto result = response.getBody();

        assertNotNull(result);

        assertAll(
                () -> assertEquals(TEST_YEAR, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testFindBudgetByYearMultipleBudgets_shouldReturnExpectedBudget() {
        Budget testBudget = getNewValidBudget();
        Budget testBudget2 = getNewValidBudget().setYear(TEST_YEAR_2);
        Budget testBudget3 = getNewValidBudget().setYear(TEST_YEAR_3);

        budgetRepository.save(testBudget);
        budgetRepository.save(testBudget2);
        budgetRepository.save(testBudget3);

        ResponseEntity<BudgetDto> response = getBudgetByYearRestCall(TEST_YEAR_2);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BudgetDto result = response.getBody();

        assertNotNull(result);

        assertAll(
                () -> assertEquals(TEST_YEAR_2, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testFindBudgetByInvalidYear_shouldReturnNotFoundStatus() {
        Budget testBudget = getNewValidBudget();
        budgetRepository.save(testBudget);

        ResponseEntity<BudgetDto> response = getBudgetByYearRestCall(INVALID_YEAR);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteBudgetByYearSingleBudget_budgetShouldBeDeleted() {
        Budget testBudget = getNewValidBudget();
        UUID testBudgetId = testBudget.getBudgetId();
        budgetRepository.save(testBudget);

        deleteBudgetByYearRestCall(TEST_YEAR);

        assertFalse(budgetRepository.existsById(testBudgetId));
    }

    @Test
    public void testDeleteBudgetByInvalidYear_budgetShouldNotBeDeleted() {
        Budget testBudget = getNewValidBudget();
        UUID testBudgetId = testBudget.getBudgetId();
        budgetRepository.save(testBudget);

        deleteBudgetByYearRestCall(INVALID_YEAR);

        assertTrue(budgetRepository.existsById(testBudgetId));
    }

    @Test
    public void testDeleteBudgetByYearMultipleBudgets_correctBudgetShouldBeDeleted() {
        Budget testBudget = getNewValidBudget();
        UUID testBudgetId = testBudget.getBudgetId();
        budgetRepository.save(testBudget);

        Budget testBudget2 = getNewValidBudget().setYear(TEST_YEAR_2);
        UUID testBudgetId2 = testBudget2.getBudgetId();
        budgetRepository.save(testBudget2);

        Budget testBudget3 = getNewValidBudget().setYear(TEST_YEAR_3);
        UUID testBudgetId3 = testBudget3.getBudgetId();
        budgetRepository.save(testBudget3);

        deleteBudgetByYearRestCall(TEST_YEAR_2);

        assertTrue(budgetRepository.existsById(testBudgetId));
        assertFalse(budgetRepository.existsById(testBudgetId2));
        assertTrue(budgetRepository.existsById(testBudgetId3));
    }

    @Test
    public void testUpdateBudgetWithExistingBudget_shouldUpdateBudgetWithNewValues() {
        Budget testBudget = getNewValidBudget();
        budgetRepository.saveAndFlush(testBudget);
        UUID testBudgetId = testBudget.getBudgetId();

        BudgetDto input = getValidBudgetDtoInput();

        assertTrue(budgetRepository.existsById(testBudgetId));

        updateBudgetByYearRestCall(TEST_YEAR, input);

        Optional<Budget> result = budgetRepository.findById(testBudgetId);

        assertTrue(result.isPresent());

        assertAll(
                () -> assertEquals(testBudgetId, result.get().getBudgetId(), "budget ID"),
                () -> assertEquals(input.getYear(), result.get().getYear(), "year"),
                () -> assertThat("expected total", input.getExpectedTotal(), comparesEqualTo(result.get().getExpectedTotal()))
        );
    }

    @Test
    public void testUpdateBudgetWithNoExistingBudget_shouldCreateNewBudget() {
        BudgetDto input = getValidBudgetDtoInput();

        assertNull(budgetRepository.findBudgetByYear(TEST_YEAR));

        updateBudgetByYearRestCall(TEST_YEAR, input);

        Budget result = budgetRepository.findBudgetByYear(TEST_YEAR);

        assertNotNull(result);

        assertAll(
                () -> assertEquals(input.getYear(), result.getYear(), "year"),
                () -> assertThat("expected total", input.getExpectedTotal(), comparesEqualTo(result.getExpectedTotal()))
        );
    }

    private void updateBudgetByYearRestCall(Integer year, BudgetDto input) {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, year);
        restTemplate.put(uri, input);
    }

    private ResponseEntity<BudgetDto> getBudgetByYearRestCall(Integer year) {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, year);

        return restTemplate.getForEntity(uri, BudgetDto.class);
    }

    private void deleteBudgetByYearRestCall(Integer year) {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, year);
        restTemplate.delete(uri);
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }

    private BudgetDto getValidBudgetDtoInput() {
        return new BudgetDto()
                .setYear(TEST_YEAR)
                .setExpectedTotal(NEW_EXPECTED_TOTAL);
    }
}
