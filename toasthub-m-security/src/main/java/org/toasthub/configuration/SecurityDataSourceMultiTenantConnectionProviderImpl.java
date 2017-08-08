package org.toasthub.configuration;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.toasthub.core.system.model.AppCacheClientDomains;
import org.toasthub.core.system.model.ClientDomain;

public class SecurityDataSourceMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	@Autowired
	@Qualifier("securityDataSourcesApplication")
	private Map<String, DataSource> securityDataSourcesApplication;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	@Override
	protected DataSource selectAnyDataSource() {
		logger.info("get any security datasource "+securityDataSourcesApplication.size());
		
		return this.securityDataSourcesApplication.values().iterator().next();
	}

	@Override
	protected DataSource selectDataSource(String tenantIdentifier) {
		logger.info("security tenant Identifier "+ tenantIdentifier);
		if ("localhost".equals(tenantIdentifier)){
			tenantIdentifier = "internet";
		}
		ClientDomain clientDomain = appCacheClientDomains.getClientDomain(tenantIdentifier);
		if (clientDomain != null) {
			tenantIdentifier = clientDomain.getAPPDomain();
		}
		logger.info("get security datasource "+ tenantIdentifier);
		return this.securityDataSourcesApplication.get(tenantIdentifier);
	}

}
