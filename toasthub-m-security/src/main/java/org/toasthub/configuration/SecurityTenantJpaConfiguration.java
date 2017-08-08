package org.toasthub.configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.toasthub.configuration.SecurityTenantProperties.Datasource;


@Configuration
@EnableConfigurationProperties({ SecurityTenantProperties.class })
@PropertySource(value = { "classpath:application.properties" })
@EnableTransactionManagement
public class SecurityTenantJpaConfiguration {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SecurityTenantProperties securityTenantProperties;
	
	@Autowired
    private Environment environment;
	
	@Bean(name = "securityDataSourcesApplication" )
	public Map<String, DataSource> securityDataSourcesApplication() {
		logger.info("securityDataSourcesApplication -- loop through datasources");
		Map<String, DataSource> result = new HashMap<>();
		for (Datasource dsProperties : securityTenantProperties.getDatasources()) {
			DataSourceBuilder factory = DataSourceBuilder
				.create()
				.url(dsProperties.getUrl())
				.username(dsProperties.getUsername())
				.password(dsProperties.getPassword())
				.driverClassName(dsProperties.getDriverClassName());
			result.put(dsProperties.getTenantId(), factory.build());
		}
		return result;
	}
	
	
	@Bean(name = "securityTenantConnectionProvider")
	public MultiTenantConnectionProvider securityTenantConnectionProvider() {
		return new SecurityDataSourceMultiTenantConnectionProviderImpl();
	}
	
	@Bean(name = "securityCurrentTenantIdentifierResolver")
	public CurrentTenantIdentifierResolver securityCurrentTenantIdentifierResolver() {
		return new SecurityTenantIdentifierResolverImpl();
	}
	
	@Bean(name = "securityEntityManagerFactoryBean")
	public LocalContainerEntityManagerFactoryBean securityEntityManagerFactoryBean(@Qualifier("securityTenantConnectionProvider") MultiTenantConnectionProvider securityTenantConnectionProvider,
		@Qualifier("securityCurrentTenantIdentifierResolver") CurrentTenantIdentifierResolver securityCurrentTenantIdentifierResolver) {
		logger.info("security entityManagerFactoryBean called");
		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		//hibernateProps.putAll(hibernateProperties());
		hibernateProps.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		hibernateProps.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, securityTenantConnectionProvider);
		hibernateProps.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, securityCurrentTenantIdentifierResolver);
		hibernateProps.put(org.hibernate.cfg.Environment.DIALECT, environment.getRequiredProperty("hibernate.multitenant.dialect"));
		hibernateProps.put(org.hibernate.cfg.Environment.SHOW_SQL, environment.getRequiredProperty("hibernate.multitenant.show_sql"));
		
		// No dataSource is set to resulting entityManagerFactoryBean
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan(new String[] {"org.toasthub.security.model"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(hibernateProps);
		em.setPersistenceUnitName("PUSecurity");
		
		return em;
	}
	
	@Bean(name = "entityManagerFactorySecurity")
	public EntityManagerFactory entityManagerFactorySecurity(@Qualifier("securityEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean securityEntityManagerFactoryBean) {
		return securityEntityManagerFactoryBean.getObject();
	}


	@Bean(name = "TransactionManagerSecurity")
	public PlatformTransactionManager TransactionManagerSecurity(@Qualifier("entityManagerFactorySecurity") EntityManagerFactory entityManagerFactorySecurity) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactorySecurity);
		return transactionManager;
	}
}
