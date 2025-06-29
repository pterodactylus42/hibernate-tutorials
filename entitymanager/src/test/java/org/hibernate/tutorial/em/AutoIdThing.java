package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AutoIdThing {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	public AutoIdThing() {
		// this form used by Hibernate
	}

	public Long getId() {
		return id;
	}


}
