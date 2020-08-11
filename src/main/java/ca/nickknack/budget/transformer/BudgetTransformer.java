package ca.nickknack.budget.transformer;

import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.dto.BudgetInputDto;
import ca.nickknack.budget.entity.Budget;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BudgetTransformer {
    public BudgetDto toBudgetDto(Budget budget) {
        return new BudgetDto()
                .setBudgetId(budget.getBudgetId())
                .setYear(budget.getYear())
                .setExpectedTotal(budget.getExpectedTotal());
    }

    public Budget toBudget(Budget budget, BudgetInputDto budgetInputDto) {
        return budget
                .setYear(budgetInputDto.getYear())
                .setExpectedTotal(budgetInputDto.getExpectedTotal());
    }
}
