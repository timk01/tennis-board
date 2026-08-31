package tennisboard.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

@Configuration
public class PersistenceConfig {

    @Bean
    public PersistenceExceptionTranslationPostProcessor processor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
