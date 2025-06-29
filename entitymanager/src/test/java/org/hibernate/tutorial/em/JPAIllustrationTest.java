/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.tutorial.em;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.hibernate.LazyInitializationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import junit.framework.TestCase;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;

import static jakarta.persistence.Persistence.createEntityManagerFactory;


/**
 * Illustrates basic use of Hibernate as a Jakarta Persistence provider.
 * Configuration properties are sourced from persistence.xml.
 *
 * @author Steve Ebersole
 */
public class JPAIllustrationTest extends TestCase {
	private EntityManagerFactory entityManagerFactory;

	@Override
	protected void setUp() {
		// an EntityManagerFactory is set up once for an application
		// IMPORTANT: notice how the name here matches the name we
		// gave the persistence-unit in persistence.xml
		entityManagerFactory = createEntityManagerFactory("org.hibernate.tutorial.jpa");
	}

	@Override
	protected void tearDown() {
		entityManagerFactory.close();
	}

	public void testBasicUsage() {
		// create a couple of events...
		inTransaction(entityManager -> {
			entityManager.persist(new Event("Our very first event!", now()));
			entityManager.persist(new Event("A follow up event", now()));
		});

		// now lets pull events from the database and list them
		inTransaction(entityManager -> {
			entityManager.createQuery("select e from Event e", Event.class).getResultList()
					.forEach(event -> out.println("Event (" + event.getDate() + ") : " + event.getTitle()));
		});
	}

	void inTransaction(Consumer<EntityManager> work) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			work.accept(entityManager);
			transaction.commit();
		}
		catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		}
		finally {
			entityManager.close();
		}
	}

	public void testGenerationStrategies() {		
		
		inTransaction(entityManager -> {
			entityManager.persist(new IdentityIdThing());
			entityManager.persist(new IdentityIdThing());
			entityManager.persist(new IdentityIdThing());

			entityManager.persist(new SequenceIdThing());
			entityManager.persist(new SequenceIdThing());
			entityManager.persist(new SequenceIdThing());

			entityManager.persist(new AutoIdThing());
			entityManager.persist(new AutoIdThing());
			entityManager.persist(new AutoIdThing());
		});

		inTransaction(entityManager -> {
			entityManager.createQuery("select i from IdentityIdThing i", IdentityIdThing.class).getResultList()
					.forEach(thing -> out.println("IdentityIdThing (" + thing.getId() + ")"));

			entityManager.createQuery("select s from SequenceIdThing s", SequenceIdThing.class).getResultList()
			.forEach(thing -> out.println("SequenceIdThing (" + thing.getId() + ")"));

			entityManager.createQuery("select a from AutoIdThing a", AutoIdThing.class).getResultList()
			.forEach(thing -> out.println("AutoIdThing (" + thing.getId() + ")"));
		});
		
	}
	

	public void testEnumeratedType() {		
		
		inTransaction(entityManager -> {
			entityManager.persist(createRoomWithPlants());
			entityManager.persist(createHouseWithPlants());
		});

		inTransaction(entityManager -> {
			entityManager.createQuery("select r from Room r", Room.class).getResultList()
					.forEach(room -> out.println("Room (" + room.getId() + ") : " + room.getPlants()));

			entityManager.createQuery("select h from House h", House.class).getResultList()
			.forEach(house -> out.println("House (" + house.getId() + ") : " + house.getPlants()));
		});
		
	}

	private Room createRoomWithPlants() {
		Room room = new Room();
		for(Plant p : Plant.values()) {
			room.addPlant(p);
		}
		return room;
	}

	private House createHouseWithPlants() {
		House house = new House();
		for(Plant p : Plant.values()) {
			house.addPlant(p);
		}
		return house;
	}

	public void testEagerLazy() {		
		
		OrganizationalUnit orgUnit = new OrganizationalUnit();
		orgUnit.addHouse(createHouseWithPlants()); // lazy
		orgUnit.addThing(new IdentityIdThing()); // eager
		
		inTransaction(entityManager -> {
			entityManager.persist(orgUnit);
		});

		List<OrganizationalUnit> units = new ArrayList<>();
		inTransaction(entityManager -> {
			List<OrganizationalUnit> resultList = entityManager.createQuery("select u from OrganizationalUnit u", OrganizationalUnit.class).getResultList();
			units.add(resultList.get(0));
		});
		
		OrganizationalUnit unitUnderTest = units.get(0);
		try {				
			for(House house : unitUnderTest.getHouses()) {
				// for loop tries to initialize the list
				out.println(house.toString());
			}
			fail();
		} catch (LazyInitializationException lie) {
			// the transaction is gone
		}
		// the things are initialized because they are fetched eager 
		out.println(unitUnderTest.getThings());
		
	}
	
	public void testCascadePersist() {
		
		OrganizationalUnit orgUnit = new OrganizationalUnit();
		orgUnit.addHouse(createHouseWithPlants()); // lazy
		orgUnit.addThing(new IdentityIdThing()); // eager
		
		inTransaction(entityManager -> {
			entityManager.persist(orgUnit);
		});
	
		inTransaction(entityManager -> {
			List<OrganizationalUnit> unitList = entityManager.createQuery("select u from OrganizationalUnit u", OrganizationalUnit.class).getResultList();
			assertEquals(unitList.size(), 1);

			List<House> houseList = entityManager.createQuery("select h from House h", House.class).getResultList();
			assertEquals(houseList.size(), 1);

			List<IdentityIdThing> thingList = entityManager.createQuery("select i from IdentityIdThing i", IdentityIdThing.class).getResultList();
			assertEquals(thingList.size(), 1);
		});

		inTransaction(entityManager -> {
			OrganizationalUnit loadedUnit = entityManager.find(OrganizationalUnit.class, orgUnit.getId());
			entityManager.remove(loadedUnit);
		});
	
		// remove is not cascaded, sub-entities remain
		inTransaction(entityManager -> {
			List<OrganizationalUnit> unitList = entityManager.createQuery("select u from OrganizationalUnit u", OrganizationalUnit.class).getResultList();
			assertEquals(unitList.size(), 0);

			List<House> houseList = entityManager.createQuery("select h from House h", House.class).getResultList();
			assertEquals(houseList.size(), 1);

			List<IdentityIdThing> thingList = entityManager.createQuery("select i from IdentityIdThing i", IdentityIdThing.class).getResultList();
			assertEquals(thingList.size(), 1);
		});

	}

	
	public void testNPlusOneUnidirectional() {
		
		out.println("---Create orgUnit");
		OrganizationalUnit orgUnit = new OrganizationalUnit();
		orgUnit.addThing(new IdentityIdThing()); 
		orgUnit.addThing(new IdentityIdThing()); 
		orgUnit.addThing(new IdentityIdThing()); 
		orgUnit.addHouse(createHouseWithPlants()); 
		orgUnit.addHouse(createHouseWithPlants()); 
		orgUnit.addHouse(createHouseWithPlants()); 
		
		out.println("---Persist orgUnit");
		inTransaction(entityManager -> {
			entityManager.persist(orgUnit);
		});
	
		out.println("---Query results");
		inTransaction(entityManager -> {
			List<OrganizationalUnit> unitList = entityManager.createQuery("select u from OrganizationalUnit u", OrganizationalUnit.class).getResultList();
			assertEquals(unitList.size(), 1);

			List<House> houseList = entityManager.createQuery("select h from House h", House.class).getResultList();
			assertEquals(houseList.size(), 3);

			List<IdentityIdThing> thingList = entityManager.createQuery("select i from IdentityIdThing i", IdentityIdThing.class).getResultList();
			assertEquals(thingList.size(), 3);
		});

		out.println("---Use results");
		inTransaction(entityManager -> {
			out.println("------load unit");
			OrganizationalUnit loadedUnit = entityManager.find(OrganizationalUnit.class, orgUnit.getId());
			// hibernate generates query for houses
			for(House h : loadedUnit.getHouses()) {
				out.println(h.toString());
			}
			// no query needed for the things, they were loaded on loading the unit
			out.println("------use things");
			for(IdentityIdThing i : loadedUnit.getThings()) {
				out.println(i.toString());
			}

		});
	
	}
	
	public void testNPlusOneBidirectional() {
		
//		@Entity
//		public class User {
//		    @Id
//		    private Long id;
//		    private String username;
//		    private String email;
//
//		    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
//		    private List<Post> posts;
//
//		    // constructors, getters, setters, etc.
//		}

//		@Entity
//		public class Post {
//		    @Id
//		    private Long id;
//		    private String content;
//
//		    @ManyToOne(fetch = FetchType.LAZY)
//		    @JoinColumn(name = "author_id")
//		    private User author;
//
//		    // constructors, getters, setters, etc.
//		}

//		@Transactional(readOnly = true)
//		public void fetchUsersAndPosts() {
//		    List<User> users = userRepository.findAll();
//		    for (User user : users) {
//		        System.out.println("User: " + user.getUsername());
//		        for (Post post : user.getPosts()) {
//		            System.out.println("Post: " + post.getContent());
//		        }
//		    }
//		}

	}


}
