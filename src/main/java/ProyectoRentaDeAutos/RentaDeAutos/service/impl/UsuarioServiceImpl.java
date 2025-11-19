package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.exception.BusinessException;
import ProyectoRentaDeAutos.RentaDeAutos.exception.ResourceNotFoundException;
import ProyectoRentaDeAutos.RentaDeAutos.models.Rol;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.repository.RolRepository;
import ProyectoRentaDeAutos.RentaDeAutos.repository.UsuarioRepository;
import ProyectoRentaDeAutos.RentaDeAutos.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                             RolRepository rolRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario registrarUsuario(Usuario usuario, String nombreRol) {
        log.info("Registrando usuario con email: {}", usuario.getEmail());

        // Validar email único
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("El email ya está registrado: " + usuario.getEmail());
        }

        // Validar DNI único
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            throw new BusinessException("El DNI ya está registrado: " + usuario.getDni());
        }

        // Validar contraseña
        if (usuario.getContra() == null || usuario.getContra().length() < 6) {
            throw new BusinessException("La contraseña debe tener al menos 6 caracteres");
        }

        // Obtener rol
        Rol rol = rolRepository.findByNombre(nombreRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", nombreRol));

        // Hashear contraseña
        usuario.setContra(passwordEncoder.encode(usuario.getContra()));
        usuario.setRol(rol);
        usuario.setEstado(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getIdUsuario());

        return usuarioGuardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerPorRol(String nombreRol) {
        return usuarioRepository.findByRolNombreAndEstadoTrue(nombreRol);
    }

    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerPorId(id);

        // Validar email si cambió
        if (!usuario.getEmail().equals(usuarioActualizado.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new BusinessException("El email ya está registrado");
            }
            usuario.setEmail(usuarioActualizado.getEmail());
        }

        // Validar DNI si cambió
        if (!usuario.getDni().equals(usuarioActualizado.getDni())) {
            if (usuarioRepository.existsByDni(usuarioActualizado.getDni())) {
                throw new BusinessException("El DNI ya está registrado");
            }
            usuario.setDni(usuarioActualizado.getDni());
        }

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setDireccion(usuarioActualizado.getDireccion());

        // Solo actualizar contraseña si se proporciona una nueva
        if (usuarioActualizado.getContra() != null && !usuarioActualizado.getContra().isEmpty()) {
            if (usuarioActualizado.getContra().length() < 6) {
                throw new BusinessException("La contraseña debe tener al menos 6 caracteres");
            }
            usuario.setContra(passwordEncoder.encode(usuarioActualizado.getContra()));
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizarPerfil(Long id, String nombre, String apellido, String dni, String telefono, String direccion, String nuevaContra) {
        log.info("Actualizando perfil del usuario: {}", id);

        Usuario usuario = obtenerPorId(id);

        // Validar DNI si cambió
        if (!usuario.getDni().equals(dni)) {
            if (usuarioRepository.existsByDni(dni)) {
                throw new BusinessException("El DNI ya está registrado");
            }
            usuario.setDni(dni);
        }

        // Actualizar campos básicos (el email NO se puede cambiar)
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);

        // Solo actualizar contraseña si se proporciona una nueva
        if (nuevaContra != null && !nuevaContra.isEmpty()) {
            if (nuevaContra.length() < 6) {
                throw new BusinessException("La contraseña debe tener al menos 6 caracteres");
            }
            usuario.setContra(passwordEncoder.encode(nuevaContra));
            log.info("Contraseña actualizada para usuario: {}", id);
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Perfil del usuario {} actualizado exitosamente", id);

        return usuarioActualizado;
    }

    @Override
    public Usuario cambiarRol(Long id, Long idRol) {
        log.info("Cambiando rol del usuario: {}", id);

        Usuario usuario = obtenerPorId(id);

        // Obtener el nuevo rol
        Rol nuevoRol = rolRepository.findById(idRol)
            .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", idRol));

        // Cambiar SOLO el rol, sin modificar la contraseña
        usuario.setRol(nuevoRol);

        // Guardar directamente el usuario
        // NO usar el método actualizarUsuario() porque re-hashearía la contraseña
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        log.info("Rol del usuario {} cambiado a: {}", id, nuevoRol.getNombre());

        return usuarioActualizado;
    }

    @Override
    public void desactivarUsuario(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado: {}", id);
    }

    @Override
    public void activarUsuario(Long id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setEstado(true);
        usuarioRepository.save(usuario);
        log.info("Usuario activado: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeDni(String dni) {
        return usuarioRepository.existsByDni(dni);
    }
}
