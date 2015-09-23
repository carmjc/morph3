package net.carmgate.morph;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import net.carmgate.morph.events.MObserves;
import net.carmgate.morph.events.db.DbChange;

public class EntityManagerProducer {

	private static EntityManagerFactory startupEntityManagerFactory = Persistence.createEntityManagerFactory("MORPH_SEED");
	private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("MORPH");

	@Inject private Logger LOGGER;

	private String db = "startup";
	private EntityManager entityManager;
	private EntityManager startupEntityManager;

	// TODO implement multi-tenant architecture
	// http://fr.slideshare.net/rcandidosilva/supporting-multitenancy-applications-with-java-ee
	// http://docs.jboss.org/hibernate/core/4.1/devguide/en-US/html/ch16.html#d5e4570
	// http://stackoverflow.com/questions/7255294/how-to-enable-multi-tenancy-in-hibernate-4-with-jpa
	@Produces
	@Singleton
	public EntityManager createEntityManager() {
		try {
			if (startupEntityManager == null) {
				startupEntityManager = EntityManagerProducer.startupEntityManagerFactory.createEntityManager();
			}
			// db = "morph";
			return startupEntityManager;
			// } else {
			// if (entityManager == null || !entityManager.isOpen()) {
			// entityManager = EntityManagerProducer.entityManagerFactory.createEntityManager();
			// }
			// return entityManager;
			// }
		} catch (RuntimeException re) {

			LOGGER.error("Error while creating EntityManager from EntityManagerFactory.", re);

			throw re;

		}
	}

	private void onEntityManagerDbChange(@MObserves DbChange dbChange) {
		LOGGER.debug("" + dbChange);
	}
}
