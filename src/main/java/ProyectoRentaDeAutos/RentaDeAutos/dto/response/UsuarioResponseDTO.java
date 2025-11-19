package ProyectoRentaDeAutos.RentaDeAutos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para datos de usuario.
 * NO incluye la contraseña por seguridad.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String dni;
    private String telefono;
    private String direccion;
    private Boolean estado;
    private String rolNombre;
}
