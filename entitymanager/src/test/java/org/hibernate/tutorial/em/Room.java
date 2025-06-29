package org.hibernate.tutorial.em;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Room {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private List<Plant> plants = new ArrayList<>();
	
	public Room() {
		// this form used by Hibernate
	}

	public Long getId() {
		return id;
	}
	
	public List<Plant> getPlants() {
		return Collections.unmodifiableList(plants);
	}
	
	public void addPlant(Plant plant) {
		plants.add(plant);
	}

}
