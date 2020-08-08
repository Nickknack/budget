package ca.nickknack.budget.core;

import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.exception.NotFoundException;
import ca.nickknack.budget.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget getBudget(Integer year) {
        Optional<Budget> budget = Optional.ofNullable(budgetRepository.findBudgetByYear(year));

        return budget.orElseThrow(() -> new NotFoundException(String.format("No budget found for year: %s", year)));
    }
}

