package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Person {

	@Id
	@GeneratedValue
	private Long id;
	
	public Person() {
		// this form used by Hibernate
	}

	public Long getId() {
		return id;
	}
	
}
