package too.sgacf.model;

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
@Table(name = "rubros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class FieldModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_rubro", nullable = false, length = 20)
    private String name;

    @Column(name = "estado", nullable = false)
    private boolean status;
}
