package ca.nickknack.budget.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class BudgetDto {
    private UUID budgetId;
    private Integer year;
    private BigDecimal expectedTotal;
}
