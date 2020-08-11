package ca.nickknack.budget.core;

import ca.nickknack.budget.dto.BudgetInputDto;
import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.exception.NotFoundException;
import ca.nickknack.budget.repository.BudgetRepository;
import ca.nickknack.budget.transformer.BudgetTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget getBudget(UUID budgetId) {
        return budgetRepository.findById(budgetId).orElseThrow(() -> new NotFoundException(String.format("No budget with id: %s", budgetId)));
    }

    public Budget updateBudget(UUID budgetId, BudgetInputDto budgetInputDto) {
        Budget budget = getBudget(budgetId);

        if (isBudgetYearUpdatingAndNotUnique(budget.getYear(), budgetInputDto.getYear())) {
            throw new IllegalArgumentException(String.format("a budget already exists for the year %s", budgetInputDto.getYear()));
        }

        return budgetRepository.save(BudgetTransformer.toBudget(getBudget(budgetId), budgetInputDto));
    }

    public void deleteBudget(UUID budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    public Budget createBudget(BudgetInputDto budgetInputDto) {
        if (budgetRepository.existsByYear(budgetInputDto.getYear())) {
            throw new IllegalArgumentException(String.format("a budget already exists for the year %s", budgetInputDto.getYear()));
        }

        return budgetRepository.save(BudgetTransformer.toBudget(Budget.newInstance(), budgetInputDto));
    }

    public Boolean isBudgetYearUpdatingAndNotUnique(Integer oldBudgetYear, Integer newBudgetYear) {
        return !oldBudgetYear.equals(newBudgetYear) && budgetRepository.existsByYear(newBudgetYear);
    }
}

