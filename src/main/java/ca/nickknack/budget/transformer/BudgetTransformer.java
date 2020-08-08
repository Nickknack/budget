package ca.nickknack.budget.transformer;

import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.entity.Budget;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BudgetTransformer {
    public BudgetDto toBudgetDto(Budget budget) {
        return new BudgetDto()
                .setYear(budget.getYear())
                .setExpectedTotal(budget.getExpectedTotal());
    }
}
