package ProyectoRentaDeAutos.RentaDeAutos.service.impl;

import ProyectoRentaDeAutos.RentaDeAutos.models.Rol;
import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import ProyectoRentaDeAutos.RentaDeAutos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios (sin base de datos) del servicio que carga usuarios para Spring Security.
 */
class CustomUserDetailsServiceTest {

    private final UsuarioRepository repo = mock(UsuarioRepository.class);
    private final CustomUserDetailsService service = new CustomUserDetailsService(repo);

    private Usuario usuario(String email, String rol, boolean activo) {
        Rol r = new Rol();
        r.setNombre(rol);
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setContra("$2a$10$hashDePrueba");
        u.setEstado(activo);
        u.setRol(r);
        return u;
    }

    @Test
    void asignaAutoridadConPrefijoRole() {
        when(repo.findByEmail("admin@test.com"))
            .thenReturn(Optional.of(usuario("admin@test.com", "ADMIN", true)));

        UserDetails details = service.loadUserByUsername("admin@test.com");

        // SecurityConfig usa hasRole("ADMIN"), que espera la autoridad ROLE_ADMIN
        assertThat(details.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly("ROLE_ADMIN");
        assertThat(details.getUsername()).isEqualTo("admin@test.com");
    }

    @Test
    void emailInexistenteLanzaExcepcion() {
        when(repo.findByEmail("nadie@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nadie@test.com"))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void usuarioDesactivadoNoPuedeLoguearse() {
        when(repo.findByEmail("baja@test.com"))
            .thenReturn(Optional.of(usuario("baja@test.com", "CLIENTE", false)));

        assertThatThrownBy(() -> service.loadUserByUsername("baja@test.com"))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
