package org.toasthub.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.toasthub.core.common.EntityManagerSecuritySvc;


@Component("EntityManagerSecuritySvc")
public class EntityManagerSecuritySvcImpl implements EntityManagerSecuritySvc {

	@PersistenceContext(unitName = "PUSecurity")
	EntityManager security;

	@Override
	public EntityManager getInstance() {
		return security;
	}

}
