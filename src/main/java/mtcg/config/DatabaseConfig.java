package mtcg.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Getter
@NoArgsConstructor
public class DatabaseConfig {

    private static final String PATH_TO_CONFIG = "database-dev.yml";

    @JsonProperty
    private String ip;
    @JsonProperty
    private String database;
    @JsonProperty
    private int port;
    @JsonProperty
    private String username;
    @JsonProperty
    private String password;
    @JsonProperty
    private int poolSize = 20;

    @SneakyThrows
    public static DatabaseConfig getConfig() {
        return new ObjectMapper(new YAMLFactory()).readValue(ClassLoader.getSystemClassLoader().getResource(PATH_TO_CONFIG), DatabaseConfig.class);
    }

    public String getConnectionString() {
        return String.format("jdbc:postgresql://%s:%d/%s", ip, port, database);
    }
}
