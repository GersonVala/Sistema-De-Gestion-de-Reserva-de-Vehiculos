package ProyectoRentaDeAutos.RentaDeAutos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización de perfil de usuario.
 * El email NO se puede cambiar (es el identificador único).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}$", message = "El DNI debe tener 7 u 8 dígitos")
    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 50, message = "La dirección no puede exceder 50 caracteres")
    private String direccion;

    // Contraseña opcional - solo si el usuario desea cambiarla
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaContra;

    // Confirmación de nueva contraseña
    private String confirmarContra;
}
