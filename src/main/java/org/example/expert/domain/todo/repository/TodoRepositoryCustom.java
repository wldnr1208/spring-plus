package org.example.expert.domain.todo.repository;

import java.util.Optional;

import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;

public interface TodoRepositoryCustom {
	Optional<Todo> findByIdWithUser(Long todoId);
	Page<TodoSearchResponse> search(TodoSearchRequest request);
}
