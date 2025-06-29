package org.hibernate.tutorial.em;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * showcases eager and lazy fetching. Persist operations are cascaded
 */
@Entity
public class OrganizationalUnit {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private List<House> houses = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	private List<IdentityIdThing> things = new ArrayList<>();	

	public OrganizationalUnit() {
		//
	}

	public Long getId() {
		return id;
	}
	
	public List<House> getHouses() {
		return Collections.unmodifiableList(houses);
	}
	
	public void addHouse(House house) {
		houses.add(house);
	}

	public List<IdentityIdThing> getThings() {
		return Collections.unmodifiableList(things);
	}
	
	public void addThing(IdentityIdThing thing) {
		things.add(thing);
	}

}
