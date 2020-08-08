package ca.nickknack.budget.controller;

import ca.nickknack.budget.core.BudgetService;
import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.transformer.BudgetTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/{year}")
    public BudgetDto getBudget(@PathVariable(name = "year") Integer year) {
        return BudgetTransformer.toBudgetDto(budgetService.getBudget(year));
    }

    @DeleteMapping("/{year}")
    public void deleteBudget(@PathVariable(name = "year") Integer year) {
       budgetService.deleteBudget(year);
    }
}
