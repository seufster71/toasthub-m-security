package org.toasthub.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.toasthub.core.common.EntityManagerMainSvc;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.system.model.AppCacheClientDomains;
import org.toasthub.core.system.model.ClientDomain;

@Component("EntityManagerMainSvc")
public class EntityManagerMainSvcImpl implements EntityManagerMainSvc {

	@PersistenceContext(unitName = "PUMain")
	EntityManager main;

	@Autowired
	AppCacheClientDomains appCacheClientDomains;

	@Override
	public EntityManager getEntityMgrMain() {
		return main;
	}

	@Override
	public String getAppName() {
		return getClientDomain().getAPPName();
	}

	@Override
	public String getAppDomain() {
		return getClientDomain().getAPPDomain();
	}
	
	@Override
	public String getCustDomain() {
		return getClientDomain().getCustDomain();
	}

	@Override
	public String getHTMLPrefix() {
		return getClientDomain().getHTMLPrefix();
	}

	@Override
	public String getPublicLayout() {
		return getClientDomain().getPublicLayout();
	}

	@Override
	public String getAdminLayout() {
		return getClientDomain().getAdminLayout();
	}

	@Override
	public String getMemberLayout() {
		return getClientDomain().getMemberLayout();
	}

	@Override
	public String getSysAdminLayout() {
		return getClientDomain().getSysAdminLayout();
	}
	
	private ClientDomain getClientDomain() {
		String url = TenantContext.getURLDomain();
		ClientDomain cdomain = appCacheClientDomains.getClientDomain(url);
		return cdomain;
	}
}
