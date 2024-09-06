package too.sgacf.model;

import java.math.BigDecimal;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_aportacion_cuentas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class AccountDepositDetailsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monto_limite_aporte", nullable = false)
    private BigDecimal amount;

    @Column(name = "estado",nullable = false)
    private boolean status;

    public AccountDepositDetailsModel(BigDecimal amount, boolean status) {
        this.amount = amount;
        this.status = status;
    }
}
