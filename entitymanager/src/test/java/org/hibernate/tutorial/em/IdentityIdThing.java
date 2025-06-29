package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class IdentityIdThing {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	public IdentityIdThing() {
		// this form used by Hibernate
	}

	public Long getId() {
		return id;
	}

}
