package com.example.seliseexam.controller;


import com.example.seliseexam.model.Task;
import com.example.seliseexam.model.User;
import com.example.seliseexam.repository.TaskRepository;
import com.example.seliseexam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {


	@Autowired
	UserRepository userRepository;

	@Autowired
	TaskRepository taskRepository;

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		try {
			List<User> users = new ArrayList<User>();
			userRepository.findAll().forEach(users::add);

			if (users.isEmpty()) {
				return new ResponseEntity<>("No Users found",HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(users, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/users")
	public ResponseEntity<?> createUsers(@RequestBody User user) {
		try {
			if (userRepository.existsByUsername(user.getUsername())) {
				return new ResponseEntity<>("Username Already Exist", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (userRepository.existsByEmail(user.getEmail())) {
				return new ResponseEntity<>("Email Already Exist", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LocalDate today = LocalDate.now();
			Instant instant = user.getDateOfBirth().toInstant();
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDate userBirthDayInLocalDateTime = instant.atZone(zoneId).toLocalDate();

			Period userAge = Period.between(userBirthDayInLocalDateTime, today);
			if(user.getUsername().length() >= 5 && !user.getUsername().contains(" ") && userAge.getYears() >= 18){
				User _user = userRepository
						.save(new User(user.getUsername(), user.getEmail(), user.getDateOfBirth(), user.getFirstName(), user.getLastName()));
				return new ResponseEntity<>(_user, HttpStatus.CREATED);
			}else{
				return new ResponseEntity<>("Your username must contains at least 5 character and no space. Oh, again ! you must be at least 18 years old !", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable("userId") long userId, @RequestBody User user) {
		Optional<User> userData = userRepository.findById(userId);

		if (userData.isPresent()) {
			User _user = userData.get();
			_user.setFirstName(user.getFirstName());
			_user.setLastName(user.getLastName());
			return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No User found",HttpStatus.NOT_FOUND);
		}
	}


	@PostMapping("/users/{userId}/tasks")
	public ResponseEntity<?> createTask(@PathVariable("userId") long userId, @RequestBody Task task) {
		try {
			Optional<User> userData = userRepository.findById(userId);
			if (userData.isPresent()) {
				Task _task = taskRepository
						.save(new Task(task.getDescription(), task.getDueDate(), task.getCompleted(), userId));
				return new ResponseEntity<>(_task, HttpStatus.CREATED);
			}else{
				return new ResponseEntity<>("No User found",HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PutMapping("/users/{userId}/tasks/{taskId}")
	public ResponseEntity<?> updateTask(@PathVariable("userId") long userId, @PathVariable("taskId") long taskId, @RequestBody Task task ) {

		try{
				Optional<User> userData = userRepository.findById(userId);
			if (userData.isPresent()) {
				Optional<Task> userTask = taskRepository.findByIdAndUserId(taskId, userId);
				if (userTask.isPresent()) {
					Task _task = userTask.get();
					_task.setDescription(task.getDescription());
					_task.setDueDate(task.getDueDate());
					_task.setCompleted(task.getCompleted());
					return new ResponseEntity<>(taskRepository.save(_task), HttpStatus.OK);
				}else{
					return new ResponseEntity<>("No Task found", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			else{
				return new ResponseEntity<>("No User found", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PutMapping("/users/{userId}/tasks/{taskId}/complete")
	public ResponseEntity<?> completeTask(@PathVariable("userId") long userId, @PathVariable("taskId") long taskId) {

		try{
			Optional<User> userData = userRepository.findById(userId);
			if (userData.isPresent()) {
				Optional<Task> userTask = taskRepository.findByIdAndUserId(taskId, userId);
				if (userTask.isPresent()) {
					Task _task = userTask.get();
					_task.setCompleted(true);
					return new ResponseEntity<>(taskRepository.save(_task), HttpStatus.OK);
					}else{
						return new ResponseEntity<>("Task not found", HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}else{
					return new ResponseEntity<>("Please make sure the task exists and the task belongs to specific user", HttpStatus.INTERNAL_SERVER_ERROR);
				}
		}
		catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/users/{userId}/tasks/{taskId}")
	public ResponseEntity<?> deleteTask(@PathVariable("userId") long userId, @PathVariable("taskId") long taskId ) {
		try{
			Optional<User> userData = userRepository.findById(userId);
			if (userData.isPresent()) {
				Optional<Task> userTask = taskRepository.findByIdAndUserId(taskId, userId);
				if (userTask.isPresent()) {
					taskRepository.deleteById(taskId);
					return new ResponseEntity<>("Successfully task deleted",HttpStatus.OK);
				}else{
					return new ResponseEntity<>("No Task found", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			else{
				return new ResponseEntity<>("No User found", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/users/{userId}/tasks")
	public ResponseEntity<?> getTasksByUserId(@PathVariable("userId") long userId, @RequestParam(name="completed", required=false) Boolean completed) {
		try {
			Optional<User> userData = userRepository.findById(userId);
			if (userData.isPresent()) {
				List<Task> _tasks = new ArrayList<Task>();
				if(completed!= null){
					_tasks = taskRepository.findByUserIdAndCompleted(userId, completed);
				}else {
					taskRepository.findByUserId(userId).forEach(_tasks::add);
				}
				if (_tasks.isEmpty()) {
					return new ResponseEntity<>("No Task found",HttpStatus.NO_CONTENT);
				}
				return new ResponseEntity<>(_tasks, HttpStatus.OK);
			}else{
				return new ResponseEntity<>("No User found", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
