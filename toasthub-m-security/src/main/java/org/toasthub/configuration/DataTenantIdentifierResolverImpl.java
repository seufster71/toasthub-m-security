package org.toasthub.configuration;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.toasthub.core.general.utils.TenantContext;

public class DataTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static String DEFAULT_TENANT_ID = "internet";
	
	@Override
	public String resolveCurrentTenantIdentifier() {
		String currentTenantId = TenantContext.getURLDomain();
		logger.info("data resolver "+ currentTenantId);
		String x = (currentTenantId != null) ? currentTenantId : DEFAULT_TENANT_ID;
		logger.info("data resolver item "+ x);
		return x;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		// TODO Auto-generated method stub
		return false;
	}

}
