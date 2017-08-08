package org.toasthub.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.toasthub.core.general.service.EntityManagerDataSvc;

@Component("EntityManagerDataSvc")
public class EntityManagerDataSvcImpl implements EntityManagerDataSvc {

	@PersistenceContext(unitName = "PUData")
	EntityManager data;


	@Override
	public EntityManager getInstance() {
		return data;
	}

}
