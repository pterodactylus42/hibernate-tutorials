/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.tutorial.annotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import jakarta.persistence.EntityExistsException;
import junit.framework.TestCase;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertThrows;

/**
 * Illustrates the use of Hibernate native APIs, including the use
 * of org.hibernate.boot for configuration and bootstrap.
 * Configuration properties are sourced from hibernate.properties.
 *
 * @author Steve Ebersole
 */
public class HibernateIllustrationTest extends TestCase {
	private SessionFactory sessionFactory;

	@Override
	protected void setUp() {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry =
				new StandardServiceRegistryBuilder()
						.build();
		try {
			sessionFactory =
					new MetadataSources(registry)
							.addAnnotatedClass(Event.class)
							.buildMetadata()
							.buildSessionFactory();
		}
		catch (Exception e) {
			// The registry would be destroyed by the SessionFactory, but we
			// had trouble building the SessionFactory so destroy it manually.
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	@Override
	protected void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	public void testBasicUsage() {
		// create a couple of events...
		sessionFactory.inTransaction(session -> {
			session.persist(new Event("Our very first event!", now()));
			session.persist(new Event("A follow up event", now()));
		});

		// now lets pull events from the database and list them
		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from Event", Event.class).getResultList()
					.forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle()));
		});
	}
	
	public void testSessionStates() {
		Session session = sessionFactory.openSession();
		// create a transient Object that has no corresponding row(s) in the database
		Event event = new Event();
		assertFalse(session.contains(event));
		
		// make the object persistent and associate it uniquely with the session
		session.persist(event);
		assertTrue(session.contains(event));
		// after flushing the session, this object has a corresponding row in the database
		
		// closing the session makes the object detached
		session.close();
		assertFalse(session.isOpen());		
		assertThrows(IllegalStateException.class, () -> session.contains(event));		
		
		Session anotherSession = sessionFactory.openSession();
		Event anotherEvent = new Event();
		assertFalse(anotherSession.contains(anotherEvent));
		anotherSession.persist(anotherEvent);
		assertTrue(anotherSession.contains(anotherEvent));
		
		anotherSession.evict(anotherEvent);
		// this does the same as session.detach(anotherEvent);
		assertThrows(EntityExistsException.class, () -> anotherSession.persist(anotherEvent));		
		
		anotherSession.close();
	}

	
	public void testSessionStatesMerge() {
		
		sessionFactory.inTransaction(session -> {
			session.persist(new Event("A very first event!", now()));
		});
		// the first event is persistent
		
		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from Event", Event.class).getResultList()
					.forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle()));
		});
		
		List<Long> dbids = new ArrayList<>();
		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from Event", Event.class).getResultList()
					.forEach(event -> dbids.add(event.getId()));
		});

		sessionFactory.inTransaction(session -> {
			// here comes another transient event
			Event anotherEvent = new Event();
			anotherEvent.setDate(LocalDateTime.now());
			anotherEvent.setTitle("A very first merged event!");
			anotherEvent.setId(dbids.get(0));
			session.merge(anotherEvent);
		});
		
		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from Event", Event.class).getResultList()
					.forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle()));
		});
		
		List<Event> events = new ArrayList<>();
		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from Event", Event.class).getResultList()
					.forEach(event -> events.add(event));
		});

		sessionFactory.inTransaction(session -> {
			// the detached event will be merged
			Event detachedEvent = events.get(0);
			detachedEvent.setTitle("A formerly detached event...");
			session.merge(detachedEvent);
		});

		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from Event", Event.class).getResultList()
					.forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle()));
		});
		
	}

}
