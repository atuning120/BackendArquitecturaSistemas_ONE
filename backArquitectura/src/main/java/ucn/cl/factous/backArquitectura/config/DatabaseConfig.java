package ucn.cl.factous.backArquitectura.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:.env")
@ConfigurationProperties
public class DatabaseConfig {
    // Esta clase ayuda a cargar el archivo .env como propiedades
}
