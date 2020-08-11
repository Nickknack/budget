package ca.nickknack.budget.controller;

import ca.nickknack.budget.core.BudgetService;
import ca.nickknack.budget.dto.BudgetDto;
import ca.nickknack.budget.dto.BudgetInputDto;
import ca.nickknack.budget.transformer.BudgetTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/{budgetId}")
    public BudgetDto getBudget(@PathVariable(name = "budgetId") UUID budgetId) {
        return BudgetTransformer.toBudgetDto(budgetService.getBudget(budgetId));
    }

    @DeleteMapping("/{budgetId}")
    public void deleteBudget(@PathVariable(name = "budgetId") UUID budgetId) {
        budgetService.deleteBudget(budgetId);
    }

    @PutMapping("/{budgetId}")
    public BudgetDto updateBudget(@PathVariable(name = "budgetId") UUID budgetId, @RequestBody BudgetInputDto budgetInputDto) {
        return BudgetTransformer.toBudgetDto(budgetService.updateBudget(budgetId, budgetInputDto));
    }

    @PostMapping("")
    public BudgetDto createBudget(@RequestBody BudgetInputDto budgetInputDto) {
        return BudgetTransformer.toBudgetDto(budgetService.createBudget(budgetInputDto));
    }
}
