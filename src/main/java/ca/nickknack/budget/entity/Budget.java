package ca.nickknack.budget.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="budget")
@Accessors(chain = true)
@Data
@Setter
public class Budget {
    @Id
    @Type(type = "uuid-char")
    @Column(name = "budget_id")
    @Setter(AccessLevel.NONE)
    private UUID budgetId;

    @Column(name = "year")
    private Integer year;

    @Column(name = "expected_total")
    private BigDecimal expectedTotal;

    public static Budget newInstance() {
        Budget instance = new Budget();

        instance.budgetId = UUID.randomUUID();

        return instance;
    }
}
