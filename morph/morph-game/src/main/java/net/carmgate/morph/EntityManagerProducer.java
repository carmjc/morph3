package net.carmgate.morph;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import net.carmgate.morph.events.DbChange;
import net.carmgate.morph.events.mgt.MObserves;

public class EntityManagerProducer {

	private static EntityManagerFactory startupEntityManagerFactory = Persistence.createEntityManagerFactory("MORPH_SEED");
	private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("MORPH");

	@Inject private Logger LOGGER;

	private String db = "startup";

	@Produces
	@Singleton
	public EntityManager createEntityManager() {
		try {
			if ("startup".equals(db)) {
				EntityManager entityManager = EntityManagerProducer.startupEntityManagerFactory.createEntityManager();
				db = "morph";
				return entityManager;
			} else {
				return EntityManagerProducer.entityManagerFactory.createEntityManager();
			}
		} catch (RuntimeException re) {

			LOGGER.error("Error while creating EntityManager from EntityManagerFactory.", re);

			throw re;

		}
	}

	private void onEntityManagerDbChange(@MObserves DbChange dbChange) {
		LOGGER.debug("" + dbChange);
	}
}
