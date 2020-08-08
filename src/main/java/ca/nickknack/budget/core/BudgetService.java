package ca.nickknack.budget.core;

import ca.nickknack.budget.entity.Budget;
import ca.nickknack.budget.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget findBudget(Integer year) {
        return budgetRepository.findBudgetByYear(year);
    }
}
