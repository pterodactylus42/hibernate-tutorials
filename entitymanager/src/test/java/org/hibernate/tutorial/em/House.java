package org.hibernate.tutorial.em;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

/**
 * contains different flavours of OneToMany
 */
@Entity
public class House {

	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.ORDINAL)
	private List<Plant> plants = new ArrayList<>();
	
	// default is a separate list mapping table House_Room
	// unidirectional, so no id field in Room is generated
	@OneToMany
	private List<Room> rooms = new ArrayList<>();
		
	// default is a separate list mapping table House_Bulb
	// bidirectional, so field house_id in Bulb is generated additionally to the table
	@OneToMany
	private List<Bulb> bulbs = new ArrayList<>();
	
	// if you specify mappedBy the separate mapping table is not created
	// owning side is Window, because house is a property of Window
	// creates field house_id in Windows table
	@OneToMany(mappedBy = "house")
	private List<Window> windows = new ArrayList<>();
	
	// with a join column the owning side is still Person, column is added there
	// default naming can be overridden
	// creates field owninghouse_id in Person table
	@OneToMany
	@JoinColumn(name = "owninghouse_id")
	private List<Person> persons = new ArrayList<>();
	
	public House() {
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

	public List<Room> getRooms() {
		return Collections.unmodifiableList(rooms);
	}
	
	public void addRoom(Room room) {
		rooms.add(room);
	}

	public List<Bulb> getBulbs() {
		return Collections.unmodifiableList(bulbs);
	}
	
	public void addBulb(Bulb bulb) {
		bulbs.add(bulb);
	}

	public List<Window> getWindows() {
		return Collections.unmodifiableList(windows);
	}
	
	public void addWindow(Window window) {
		windows.add(window);
	}

	public List<Person> getPersons() {
		return Collections.unmodifiableList(persons);
	}
	
	public void addPlant(Person person) {
		persons.add(person);
	}

}
