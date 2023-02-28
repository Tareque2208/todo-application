package com.example.seliseexam.repository;


import com.example.seliseexam.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUserIdAndCompleted(long userId, boolean completed);
  Optional<Task>  findByIdAndUserId(long taskId, long userId);
  List<Task> findByUserId(long userId);

}
