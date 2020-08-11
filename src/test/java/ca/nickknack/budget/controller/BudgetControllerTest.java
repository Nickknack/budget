package ca.nickknack.budget.controller;

import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.dto.BudgetInputDto;
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
    private static final Integer NEW_TEST_YEAR = 2023;

    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");
    private static final BigDecimal NEW_EXPECTED_TOTAL = new BigDecimal("60000");

    private static final UUID INVALID_ID = UUID.fromString("b543fd8f-8171-4a52-8399-f38a1a2af145");

    private UUID testBudgetId;
    private UUID testBudgetId2;
    private UUID testBudgetId3;

    @BeforeEach
    public void setupData() {
        budgetRepository.deleteAll();

        Budget testBudget = getNewValidBudget();
        testBudgetId = testBudget.getBudgetId();
        budgetRepository.save(testBudget);

        Budget testBudget2 = getNewValidBudget().setYear(TEST_YEAR_2);
        testBudgetId2 = testBudget2.getBudgetId();
        budgetRepository.save(testBudget2);

        Budget testBudget3 = getNewValidBudget().setYear(TEST_YEAR_3);
        testBudgetId3 = testBudget3.getBudgetId();
        budgetRepository.save(testBudget3);
    }

    @Test
    public void testGetBudget_shouldReturnExpectedBudget() {
        ResponseEntity<BudgetDto> response = getBudgetRestCall(testBudgetId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BudgetDto result = response.getBody();

        assertNotNull(result);

        assertAll(
                () -> assertEquals(testBudgetId, result.getBudgetId(), "budget ID"),
                () -> assertEquals(TEST_YEAR, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testGetBudgetMultipleBudgets_shouldReturnExpectedBudget() {
        ResponseEntity<BudgetDto> response = getBudgetRestCall(testBudgetId2);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BudgetDto result = response.getBody();

        assertNotNull(result);

        assertAll(
                () -> assertEquals(testBudgetId2, result.getBudgetId(), "budget ID"),
                () -> assertEquals(TEST_YEAR_2, result.getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testGetBudgetByInvalidId_shouldReturnNotFoundStatus() {
        ResponseEntity<BudgetDto> response = getBudgetRestCall(INVALID_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteBudgetSingleBudget_budgetShouldBeDeleted() {
        deleteBudgetRestCall(testBudgetId);

        assertFalse(budgetRepository.existsById(testBudgetId));
    }

    @Test
    public void testDeleteBudgetByInvalidId_budgetShouldNotBeDeleted() {
        deleteBudgetRestCall(INVALID_ID);

        assertTrue(budgetRepository.existsById(testBudgetId));
    }

    @Test
    public void testDeleteBudgetMultipleBudgets_correctBudgetShouldBeDeleted() {
        deleteBudgetRestCall(testBudgetId2);

        assertTrue(budgetRepository.existsById(testBudgetId));
        assertFalse(budgetRepository.existsById(testBudgetId2));
        assertTrue(budgetRepository.existsById(testBudgetId3));
    }

    @Test
    public void testUpdateBudgetWithExistingBudget_shouldUpdateBudgetWithNewValues() {
        BudgetInputDto input = getValidBudgetDtoInput();

        assertTrue(budgetRepository.existsById(testBudgetId));

        updateBudgetByYearRestCall(testBudgetId, input);

        Optional<Budget> result = budgetRepository.findById(testBudgetId);

        assertTrue(result.isPresent());

        assertAll(
                () -> assertEquals(testBudgetId, result.get().getBudgetId(), "budget ID"),
                () -> assertEquals(input.getYear(), result.get().getYear(), "year"),
                () -> assertThat("expected total", input.getExpectedTotal(), comparesEqualTo(result.get().getExpectedTotal()))
        );
    }

    @Test
    public void testUpdateBudgetWithNewYearNoExistingBudgetWithYear_shouldUpdateBudgetWithNewValues() {
        BudgetInputDto input = getValidBudgetDtoInput().setYear(NEW_TEST_YEAR);

        assertTrue(budgetRepository.existsById(testBudgetId));

        updateBudgetByYearRestCall(testBudgetId, input);

        Optional<Budget> result = budgetRepository.findById(testBudgetId);

        assertTrue(result.isPresent());

        assertAll(
                () -> assertEquals(testBudgetId, result.get().getBudgetId(), "budget ID"),
                () -> assertEquals(input.getYear(), result.get().getYear(), "year"),
                () -> assertThat("expected total", input.getExpectedTotal(), comparesEqualTo(result.get().getExpectedTotal()))
        );
    }

    @Test
    public void testUpdateBudgetWithNewYearExistingBudgetWithYear_shouldNotUpdateBudget() {
        BudgetInputDto input = getValidBudgetDtoInput().setYear(TEST_YEAR_2);

        assertTrue(budgetRepository.existsById(testBudgetId));

        updateBudgetByYearRestCall(testBudgetId, input);

        Optional<Budget> result = budgetRepository.findById(testBudgetId);

        assertTrue(result.isPresent());

        assertAll(
                () -> assertEquals(testBudgetId, result.get().getBudgetId(), "budget ID"),
                () -> assertEquals(TEST_YEAR, result.get().getYear(), "year"),
                () -> assertThat("expected total", TEST_EXPECTED_TOTAL, comparesEqualTo(result.get().getExpectedTotal()))
        );
    }

    @Test
    public void testCreateValidBudget_shouldCreateNewBudget() {
        BudgetInputDto input = getValidBudgetDtoInput().setYear(NEW_TEST_YEAR);

        assertFalse(budgetRepository.existsByYear(input.getYear()));

        ResponseEntity<BudgetDto> response = createBudgetRestCall(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BudgetDto result = response.getBody();

        assertNotNull(result);

        assertAll(
                () -> assertNotNull(result.getBudgetId(), "budget ID"),
                () -> assertEquals(input.getYear(), result.getYear(), "year"),
                () -> assertThat("expected total", input.getExpectedTotal(), comparesEqualTo(result.getExpectedTotal()))
        );
    }

    @Test
    public void testCreateBudgetWithExistingYear_shouldReturn500StatusAndNotCreateBudget() {
        BudgetInputDto input = getValidBudgetDtoInput();

        assertTrue(budgetRepository.existsByYear(input.getYear()));

        Integer sizeBefore = budgetRepository.findAll().size();

        ResponseEntity<BudgetDto> response = createBudgetRestCall(input);

        Integer sizeAfter = budgetRepository.findAll().size();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(sizeBefore, sizeAfter);
    }

    private void updateBudgetByYearRestCall(UUID budgetId, BudgetInputDto input) {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, budgetId);
        restTemplate.put(uri, input);
    }

    private ResponseEntity<BudgetDto> getBudgetRestCall(UUID budgetId) {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, budgetId);

        return restTemplate.getForEntity(uri, BudgetDto.class);
    }

    private void deleteBudgetRestCall(UUID budgetId) {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, budgetId);
        restTemplate.delete(uri);
    }

    private ResponseEntity<BudgetDto> createBudgetRestCall(BudgetInputDto budgetInputDto) {
        String uri = String.format("http://localhost:%s/api/v1/budget", port);
        return restTemplate.postForEntity(uri, budgetInputDto, BudgetDto.class);
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }

    private BudgetInputDto getValidBudgetDtoInput() {
        return new BudgetInputDto()
                .setYear(TEST_YEAR)
                .setExpectedTotal(NEW_EXPECTED_TOTAL);
    }
}
