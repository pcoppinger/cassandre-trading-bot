package tech.cassandre.trading.bot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * DatabaseAutoConfiguration configures the database.
 */
@Configuration
@EnableAsync
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@EntityScan(basePackages = "tech.cassandre.trading.bot.domain")
@EnableJpaRepositories(basePackages = "tech.cassandre.trading.bot.repository")
@EnableTransactionManagement
@RequiredArgsConstructor
public class DatabaseAutoConfiguration extends BaseConfiguration {

    /** Precision. */
    public static final int PRECISION = 16;

    /** Scale. */
    public static final int SCALE = 8;

    /**
     * Makes ZonedDateTime compatible with auditing fields.
     *
     * @return DateTimeProvider
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }
}
