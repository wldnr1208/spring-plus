package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    @Query("SELECT t FROM Todo t " +
        "LEFT JOIN FETCH t.user " +
        "WHERE (:weather is null OR t.weather = :weather) " +
        "AND (:createdAt is null OR t.modifiedAt >= :createdAt) " +
        "AND (:modifiedAt is null OR t.modifiedAt <= :modifiedAt) " +
        "ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllWithCondition(
        @Param("weather") String weather,
        @Param("createdAt") LocalDateTime createdAt,
        @Param("modifiedAt") LocalDateTime modifiedAt,
        Pageable pageable
    );
}
