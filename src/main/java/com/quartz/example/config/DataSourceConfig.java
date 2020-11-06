package com.quartz.example.config;

import org.postgresql.PGProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    public static final String HIBERNATE_PHYSICAL_NAMING_STRATEGY = "hibernate.physical_naming_strategy";
    public static final String HIBERNATE_IMPLICIT_NAMING_STRATEGY = "hibernate.implicit_naming_strategy";

    @Value("${postgres.host}")
    private String host;

    @Value("${postgres.port}")
    private String port;

    @Value("${postgres.database}")
    private String database;

    @Value("${postgres.user}")
    private String username;

    @Value("${postgres.password}")
    private String password;

    @Value("${postgres.maxPoolSize}")
    private int maxPoolSize;

    @Value("${postgres.minPoolSize}")
    private int minPoolSize;

    private static final String POSTGRES_DRIVER_NAME = "org.postgresql.Driver";

    private static final String URL_PATTERN = "jdbc:postgresql://%s:%s/%s";

    @Bean
    public DataSource dataSource() {
        String urlValue = String.format(URL_PATTERN, host, port, database);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(urlValue);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(POSTGRES_DRIVER_NAME);
        hikariConfig.addDataSourceProperty(PGProperty.APPLICATION_NAME.getName(), "QUARTZ_DEMO");

        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minPoolSize);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            javax.sql.DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties())
                .packages("com.quartz.example.domain", "com.quartz.example.repository")
                .build();
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(HIBERNATE_PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class.getName());
        props.put(HIBERNATE_IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class.getName());
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        return props;
    }

    @Bean
    public EntityManager postgresEntityManager(EntityManagerFactory entityManagerFactory) {
        return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }

    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
