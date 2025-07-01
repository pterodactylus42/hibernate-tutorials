package org.hibernate.tutorial.em;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users") // needed because 'User' (which would be the default table name) is a sql keyword
public class User {

	@Id
	@GeneratedValue
	private Long id;
	
	private String username;
	
	private String email;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
	private List<Post> posts = new ArrayList<>();

	User(){}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	};


}
