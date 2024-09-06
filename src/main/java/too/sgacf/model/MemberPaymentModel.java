package too.sgacf.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "pago_asociados")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class MemberPaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monto_miembro_asociado", nullable = false)
    private BigDecimal amount;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime currentDate;

    @Column(name = "estado_pago_asociado")
    private boolean paymentStatus;

    @Column(name = "estado",nullable = false)
    private boolean status;

    public MemberPaymentModel(BigDecimal amount, boolean paymentStatus, boolean status) {
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }    
}
