package org.sumit.springdemo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity  // Marks this as a JPA entity representing a DB table
  // Lombok generates getters, setters, and a no-arg constructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    // Primary key with auto-increment strategy
    private Long id;

    private String title;  // Title of the post

    public Post(Long id, String title, String body, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }

    public Post() {
        // Default constructor for JPA
    } 
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Post id(Long id) {
        setId(id);
        return this;
    }

    public Post title(String title) {
        setTitle(title);
        return this;
    }

    public Post body(String body) {
        setBody(body);
        return this;
    }

    public Post createdAt(LocalDateTime createdAt) {
        setCreatedAt(createdAt);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(body, post.body) && Objects.equals(createdAt, post.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, createdAt);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", title='" + getTitle() + "'" +
            ", body='" + getBody() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }

    @Column(columnDefinition = "text")  
    // This tells the DB that 'body' can be long text, not just varchar
    private String body;

    private LocalDateTime createdAt;  
    // Stores date and time when the post was created


    @ManyToOne
    @JoinColumn(name = "account_id",referencedColumnName="id" ,nullable = false)
    private Account account;
    // Many posts can belong to one account, so this is a many-to-one relationship
    public Account getAccount() {
        return this.account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
}
