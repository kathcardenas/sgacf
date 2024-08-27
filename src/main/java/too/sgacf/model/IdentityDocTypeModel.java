package too.sgacf.model;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_documento_identidad")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class IdentityDocTypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_tipo_doc_identidad",nullable = false, length = 20)
    private String name;

    @Column(name = "estado",nullable = false)
    private boolean status;

    public IdentityDocTypeModel(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

}
