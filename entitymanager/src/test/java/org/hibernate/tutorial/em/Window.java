package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Windows") // needed because 'Window' (which would be the default table name) is a sql keyword
public class Window {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private House house;
	
	public Window() {
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
