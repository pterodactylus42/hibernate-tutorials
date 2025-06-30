package org.hibernate.tutorial.em;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name = "PostComment")
@Table(name = "post_comment")
public class PostComment {
 
    @Id
	@GeneratedValue
    private Long id;
 
    @ManyToOne
    private Post post;
 
    private String review;
    
    PostComment(){}

	public Long getId() {
		return id;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

    
}