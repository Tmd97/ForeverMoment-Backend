package com.example.moment_forever.configs;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

    @Configuration
    @EnableTransactionManagement
    @EnableJpaAuditing
    @EnableJpaRepositories(basePackages = "com.example.demo.repository")
    public class DatabaseConfig {

        @Value("${spring.datasource.url}")
        private String url;

        @Value("${spring.datasource.username}")
        private String username;

        @Value("${spring.datasource.password}")
        private String password;

        @Value("${spring.datasource.driver-class-name}")
        private String driverClassName;

        @Value("${spring.datasource.hikari.maximum-pool-size:20}")
        private int maximumPoolSize;

        @Value("${spring.datasource.hikari.minimum-idle:5}")
        private int minimumIdle;

        @Value("${spring.datasource.hikari.connection-timeout:30000}")
        private long connectionTimeout;

        @Value("${spring.datasource.hikari.idle-timeout:600000}")
        private long idleTimeout;

        @Value("${spring.datasource.hikari.max-lifetime:1800000}")
        private long maxLifetime;

        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(driverClassName);
            dataSource.setMaximumPoolSize(maximumPoolSize);
            dataSource.setMinimumIdle(minimumIdle);
            dataSource.setConnectionTimeout(connectionTimeout);
            dataSource.setIdleTimeout(idleTimeout);
            dataSource.setMaxLifetime(maxLifetime);
            dataSource.setPoolName("Moment-Forever-HikariPool");
            dataSource.setConnectionTestQuery("SELECT 1");
            dataSource.setAutoCommit(false);

            // Connection leak detection
            dataSource.setLeakDetectionThreshold(60000);

            return dataSource;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource());
            em.setPackagesToScan("com.example.demo.entity");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

            Map<String, Object> jpaProperties = new HashMap<>();
            jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            jpaProperties.put("hibernate.hbm2ddl.auto", "update");
            jpaProperties.put("hibernate.show_sql", true);
            jpaProperties.put("hibernate.format_sql", true);
            jpaProperties.put("hibernate.jdbc.batch_size", 20);
            jpaProperties.put("hibernate.order_inserts", true);
            jpaProperties.put("hibernate.order_updates", true);
            jpaProperties.put("hibernate.query.in_clause_parameter_padding", true);
            jpaProperties.put("hibernate.connection.provider_disables_autocommit", true);

            // Performance optimizations
            jpaProperties.put("hibernate.cache.use_second_level_cache", false);
            jpaProperties.put("hibernate.cache.use_query_cache", false);
            jpaProperties.put("hibernate.generate_statistics", false);

            em.setJpaPropertyMap(jpaProperties);

            return em;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return transactionManager;
        }
}
