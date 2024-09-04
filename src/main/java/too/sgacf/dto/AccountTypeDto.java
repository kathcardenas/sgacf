package too.sgacf.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountTypeDto {

    private Long id;
    
    @NotBlank(message = "El nombre no puede ser vacío.")
    @Size(max = 20, message = "Debe tener un máximo de 20 letras.")
    private String name;

    @AssertTrue(message = "El estado debe estar activo")
    private boolean status;
}
