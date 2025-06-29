package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SequenceIdThing {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;

	public SequenceIdThing() {
		// this form used by Hibernate
	}

	public Long getId() {
		return id;
	}

}
