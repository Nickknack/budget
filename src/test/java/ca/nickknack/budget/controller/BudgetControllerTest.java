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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    private static final BigDecimal TEST_EXPECTED_TOTAL = new BigDecimal("40000");

    private static final Integer INVALID_YEAR = 1000;

    @BeforeEach
    public void clearData() {
        budgetRepository.deleteAll();
    }

    @Test
    public void testFindBudgetByYear_shouldReturnExpectedBudget() {
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, TEST_YEAR);

        Budget testBudget = getNewValidBudget();
        budgetRepository.save(testBudget);

        ResponseEntity<BudgetDto> response = restTemplate.getForEntity(uri, BudgetDto.class);

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
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, TEST_YEAR_2);

        Budget testBudget1 = getNewValidBudget();
        Budget testBudget2 = getNewValidBudget().setYear(TEST_YEAR_2);
        Budget testBudget3 = getNewValidBudget().setYear(TEST_YEAR_3);

        budgetRepository.save(testBudget1);
        budgetRepository.save(testBudget2);
        budgetRepository.save(testBudget3);

        ResponseEntity<BudgetDto> response = restTemplate.getForEntity(uri, BudgetDto.class);

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
        String uri = String.format("http://localhost:%s/api/v1/budget/%s", port, INVALID_YEAR);

        Budget testBudget = getNewValidBudget();
        budgetRepository.save(testBudget);

        ResponseEntity<BudgetDto> response = restTemplate.getForEntity(uri, BudgetDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Budget getNewValidBudget() {
        return Budget.newInstance()
                .setYear(TEST_YEAR)
                .setExpectedTotal(TEST_EXPECTED_TOTAL);
    }
}
