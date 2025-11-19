package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Servicio personalizado para cargar usuarios desde la base de datos
 * para Spring Security. Implementa la autenticación basada en email.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Cargando usuario por email: {}", email);

        // Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Validar que el usuario esté activo
        if (!usuario.getEstado()) {
            throw new UsernameNotFoundException("Usuario desactivado: " + email);
        }

        log.debug("Usuario encontrado: {} con rol: {}", email, usuario.getRol().getNombre());

        // Convertir a UserDetails de Spring Security
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getContra())
            .authorities(getAuthorities(usuario))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!usuario.getEstado())
            .build();
    }

    /**
     * Convierte el rol del usuario a GrantedAuthority de Spring Security.
     * Spring Security requiere el prefijo "ROLE_" para los roles.
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        String roleName = usuario.getRol().getNombre();

        // Spring Security requiere el prefijo ROLE_
        String authority = "ROLE_" + roleName;

        log.debug("Asignando autoridad: {}", authority);

        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}
