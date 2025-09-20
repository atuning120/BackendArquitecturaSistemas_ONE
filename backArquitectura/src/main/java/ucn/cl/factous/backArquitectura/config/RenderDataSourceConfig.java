package ucn.cl.factous.backArquitectura.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("render")
public class RenderDataSourceConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        // Convertir la URL de Render de postgresql:// a jdbc:postgresql://
        String jdbcUrl = convertRenderUrlToJdbc(databaseUrl);
        
        properties.setUrl(jdbcUrl);
        properties.setDriverClassName("org.postgresql.Driver");
        
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return dataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Convierte la URL de Render (postgresql://) al formato JDBC (jdbc:postgresql://)
     */
    private String convertRenderUrlToJdbc(String renderUrl) {
        if (renderUrl == null || renderUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL no está configurada");
        }
        
        // Si ya tiene el prefijo jdbc:, no hacer nada
        if (renderUrl.startsWith("jdbc:")) {
            return renderUrl;
        }
        
        // Si empieza con postgresql://, convertir a jdbc:postgresql://
        if (renderUrl.startsWith("postgresql://")) {
            return "jdbc:" + renderUrl;
        }
        
        // Si empieza con postgres://, convertir a jdbc:postgresql://
        if (renderUrl.startsWith("postgres://")) {
            return renderUrl.replace("postgres://", "jdbc:postgresql://");
        }
        
        throw new RuntimeException("Formato de DATABASE_URL no reconocido: " + renderUrl);
    }
}