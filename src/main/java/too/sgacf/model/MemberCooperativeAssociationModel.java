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
@Table(name = "catalogo_asociación")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class MemberCooperativeAssociationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_asociacion", length = 20)
    private String name;

    @Column(name = "estado", nullable = false)
    private boolean status;

    public MemberCooperativeAssociationModel(String name, boolean status) {
        this.name = name;
        this.status = status;
    }
}
