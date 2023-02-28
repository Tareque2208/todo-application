package com.example.seliseexam.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "description")
	private String description;

	@Column(name = "dueDate")
	private Date dueDate;

	@Column(name = "completed")
	private Boolean completed;

	@Column(name = "user_id")
	private long userId;

	public Task() {

	}

	public Task(String description, Date dueDate, Boolean completed, long userId) {
		this.description = description;
		this.dueDate = dueDate;
		this.completed = completed;
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Task { " +
				"id=" + id +
				", description='" + description + '\'' +
				", dueDate=" + dueDate +
				", completed=" + completed +
				", userId=" + userId +
				']';
	}
}
