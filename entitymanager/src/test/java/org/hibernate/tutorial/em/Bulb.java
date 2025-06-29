package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Bulb {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private House house;
	
	public Bulb() {
		// this form used by Hibernate
	}

	public Long getId() {
		return id;
	}
	
	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

}
