package ca.nickknack.budget.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BudgetInputDto {
    private Integer year;
    private BigDecimal expectedTotal;
}
