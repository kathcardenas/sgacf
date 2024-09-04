package too.sgacf.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithdrawalDetailsDto {
    private Long id;
    
    @Min(value = 0, message="El monto debe ser mayor a cero.")
    @Digits(integer = 10, fraction = 2, message = "El monto no debe tener más de 10 enteros y 2 décimales.")
    @NotNull(message = "El monto no debe ser vacío.")
    @Positive(message = "El monto debe ser positivo o mayor a cero.")
    private BigDecimal amount;

    @AssertTrue(message = "El estado debe estar activo.")
    private boolean status;
}
