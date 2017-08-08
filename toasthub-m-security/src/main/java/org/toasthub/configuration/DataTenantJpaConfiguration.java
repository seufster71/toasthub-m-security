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

import org.toasthub.configuration.DataTenantProperties.Datasource;


@Configuration
@EnableConfigurationProperties({ DataTenantProperties.class })
@PropertySource(value = { "classpath:application.properties" })
@EnableTransactionManagement
public class DataTenantJpaConfiguration {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataTenantProperties dataTenantProperties;
	
	@Autowired
    private Environment environment;
	
	@Bean(name = "dataDataSourcesApplication" )
	public Map<String, DataSource> dataDataSourcesApplication() {
		logger.info("dataSourcesApplication -- loop through datasources");
		Map<String, DataSource> result = new HashMap<>();
		for (Datasource dsProperties : dataTenantProperties.getDatasources()) {
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
	
	
	@Bean(name = "dataTenantConnectionProvider")
	public MultiTenantConnectionProvider dataTenantConnectionProvider() {
		return new DataDataSourceMultiTenantConnectionProviderImpl();
	}
	
	@Bean(name = "dataCurrentTenantIdentifierResolver")
	public CurrentTenantIdentifierResolver dataCurrentTenantIdentifierResolver() {
		return new DataTenantIdentifierResolverImpl();
	}
	
	@Bean(name = "dataEntityManagerFactoryBean")
	public LocalContainerEntityManagerFactoryBean dataEntityManagerFactoryBean(@Qualifier("dataTenantConnectionProvider") MultiTenantConnectionProvider dataTenantConnectionProvider,
		@Qualifier("dataCurrentTenantIdentifierResolver") CurrentTenantIdentifierResolver dataCurrentTenantIdentifierResolver) {
		logger.info("data entityManagerFactoryBean called");
		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		//hibernateProps.putAll(hibernateProperties());
		hibernateProps.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		hibernateProps.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, dataTenantConnectionProvider);
		hibernateProps.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, dataCurrentTenantIdentifierResolver);
		hibernateProps.put(org.hibernate.cfg.Environment.DIALECT, environment.getRequiredProperty("hibernate.multitenant.dialect"));
		hibernateProps.put(org.hibernate.cfg.Environment.SHOW_SQL, environment.getRequiredProperty("hibernate.multitenant.show_sql"));
		
		// No dataSource is set to resulting entityManagerFactoryBean
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan(new String[] {"org.toasthub.core.general.model","org.toasthub.core.preference.model"});
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(hibernateProps);
		em.setPersistenceUnitName("PUData");
		
		return em;
	}
	
	@Bean(name = "entityManagerFactoryData")
	public EntityManagerFactory entityManagerFactoryData(@Qualifier("dataEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean dataEntityManagerFactoryBean) {
		return dataEntityManagerFactoryBean.getObject();
	}


	@Bean(name = "TransactionManagerData")
	public PlatformTransactionManager TransactionManagerData(@Qualifier("entityManagerFactoryData") EntityManagerFactory entityManagerFactoryData) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryData);
		return transactionManager;
	}
}
