package ProyectoRentaDeAutos.RentaDeAutos.service;

import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario registrarUsuario(Usuario usuario, String nombreRol);

    Usuario obtenerPorId(Long id);

    Usuario obtenerPorEmail(String email);

    List<Usuario> obtenerTodos();

    List<Usuario> obtenerPorRol(String nombreRol);

    Usuario actualizarUsuario(Long id, Usuario usuario);

    Usuario actualizarPerfil(Long id, String nombre, String apellido, String dni, String telefono, String direccion, String nuevaContra);

    Usuario cambiarRol(Long id, Long idRol);

    void desactivarUsuario(Long id);

    void activarUsuario(Long id);

    boolean existeEmail(String email);

    boolean existeDni(String dni);
}
