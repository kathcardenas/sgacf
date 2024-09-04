package too.sgacf.model;

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
@Table(name = "tipo_cuentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_tipo_cuenta",nullable = false, length = 20)
    private String name;

    @Column(name = "estado",nullable = false)
    private boolean status;

    public AccountTypeModel(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

}
