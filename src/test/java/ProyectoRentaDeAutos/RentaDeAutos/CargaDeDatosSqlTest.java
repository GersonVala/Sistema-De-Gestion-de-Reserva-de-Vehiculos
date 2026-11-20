package ProyectoRentaDeAutos.RentaDeAutos;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que los datos de ejemplo (scrpts_base_de_datos/CargaDeDatos.sql)
 * sean compatibles con la configuración de seguridad de la aplicación:
 * - los nombres de rol coinciden con los usados en SecurityConfig (hasRole)
 * - las contraseñas están hasheadas con BCrypt y corresponden a "Nombre123"
 */
class CargaDeDatosSqlTest {

    private static final Path SCRIPT = Path.of("scrpts_base_de_datos", "CargaDeDatos.sql");

    private String leerScript() throws IOException {
        return Files.readString(SCRIPT, StandardCharsets.UTF_8);
    }

    @Test
    void rolesCoincidenConSecurityConfig() throws IOException {
        // Solo el bloque "INSERT INTO roles ... VALUES ... ;"
        Matcher bloque = Pattern.compile("INSERT INTO roles[^;]*;", Pattern.CASE_INSENSITIVE).matcher(leerScript());
        assertThat(bloque.find()).as("CargaDeDatos.sql debe insertar roles").isTrue();

        Matcher m = Pattern.compile("\\(\\d+,\\s*'([A-Z]+)',\\s*'[^']*'\\)").matcher(bloque.group());
        List<String> roles = new ArrayList<>();
        while (m.find()) {
            roles.add(m.group(1));
        }

        assertThat(roles).containsExactlyInAnyOrder("ADMIN", "VENDEDOR", "CLIENTE");
    }

    @Test
    void contraseniasEstanHasheadasConBCrypt() throws IOException {
        // (id,'Nombre','Apellido','email','contra', ...
        Matcher m = Pattern.compile("\\(\\d+,'(\\w+)','\\w+','[^']+@example\\.com','([^']+)'")
            .matcher(leerScript());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        int usuarios = 0;
        while (m.find()) {
            String nombre = m.group(1);
            String hash = m.group(2);
            assertThat(hash).as("contraseña de %s", nombre).startsWith("$2a$");
            assertThat(encoder.matches(nombre + "123", hash))
                .as("el hash de %s corresponde a %s123", nombre, nombre)
                .isTrue();
            usuarios++;
        }

        assertThat(usuarios).isEqualTo(10);
    }
}
