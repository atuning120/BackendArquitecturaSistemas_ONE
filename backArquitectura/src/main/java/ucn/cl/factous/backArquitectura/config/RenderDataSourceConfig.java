package ucn.cl.factous.backArquitectura.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
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
    public DataSource dataSource() {
        // Convertir la URL de Render al formato JDBC
        String jdbcUrl = convertRenderUrlToJdbc(databaseUrl);
        
        // Configurar HikariCP manualmente para tener control total
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración optimizada para Render
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(20000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        
        System.out.println("🔗 Configurando DataSource para Render con URL: " + jdbcUrl);
        
        return new HikariDataSource(config);
    }

    /**
     * Convierte la URL de Render (postgresql://) al formato JDBC (jdbc:postgresql://)
     */
    private String convertRenderUrlToJdbc(String renderUrl) {
        if (renderUrl == null || renderUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL no está configurada");
        }
        
        System.out.println("🔍 URL original de Render: " + renderUrl);
        
        // Si ya tiene el prefijo jdbc:, no hacer nada
        if (renderUrl.startsWith("jdbc:")) {
            return renderUrl;
        }
        
        // Si empieza con postgresql://, convertir a jdbc:postgresql://
        if (renderUrl.startsWith("postgresql://")) {
            String jdbcUrl = "jdbc:" + renderUrl;
            System.out.println("✅ URL convertida a JDBC: " + jdbcUrl);
            return jdbcUrl;
        }
        
        // Si empieza con postgres://, convertir a jdbc:postgresql://
        if (renderUrl.startsWith("postgres://")) {
            String jdbcUrl = renderUrl.replace("postgres://", "jdbc:postgresql://");
            System.out.println("✅ URL convertida a JDBC: " + jdbcUrl);
            return jdbcUrl;
        }
        
        throw new RuntimeException("Formato de DATABASE_URL no reconocido: " + renderUrl);
    }
}