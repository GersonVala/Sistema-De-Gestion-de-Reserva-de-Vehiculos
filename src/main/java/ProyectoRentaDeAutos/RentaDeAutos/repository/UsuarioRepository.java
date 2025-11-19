package ProyectoRentaDeAutos.RentaDeAutos.repository;

import ProyectoRentaDeAutos.RentaDeAutos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para login
    Optional<Usuario> findByEmail(String email);

    // Para validaciones de unicidad
    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    // Buscar usuarios por rol
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :nombreRol AND u.estado = true")
    List<Usuario> findByRolNombreAndEstadoTrue(@Param("nombreRol") String nombreRol);

    // Buscar usuarios activos
    List<Usuario> findByEstadoTrue();

    // Buscar por DNI
    Optional<Usuario> findByDni(String dni);
}
