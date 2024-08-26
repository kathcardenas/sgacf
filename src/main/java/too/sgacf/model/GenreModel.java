package too.sgacf.model;

import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "generos")
@Audited
@Data
@NoArgsConstructor
public class GenreModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_genero", nullable = false, length = 10)
    private String name;

    @Column(name = "estado",nullable = false)
    private boolean status;

    public GenreModel(String name, boolean status) {
        this.name = name;
        this.status = status;
    }
}
