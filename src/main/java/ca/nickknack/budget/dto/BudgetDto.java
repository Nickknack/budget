package ca.nickknack.budget.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BudgetDto {
    private Integer year;
    private BigDecimal expectedTotal;
}
