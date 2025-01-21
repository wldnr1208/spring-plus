package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// TodoRepositoryCustomImpl.java
@Repository
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public TodoRepositoryCustomImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;

		Todo result = queryFactory.selectFrom(todo)
			.leftJoin(todo.user, user).fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public Page<TodoSearchResponse> search(TodoSearchRequest request) {
		QTodo todo = QTodo.todo;
		QManager manager = QManager.manager;
		QComment comment = QComment.comment;

		// 전체 쿼리 생성
		JPAQuery<TodoSearchResponse> query = queryFactory
			.select(new QTodoSearchResponse(
				todo.title,
				manager.count(),
				comment.count()
			))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(todo.comments, comment)
			.where(
				titleContains(request.getKeyword()),
				betweenCreatedAt(request.getStartDate(), request.getEndDate()),
				managerNicknameContains(request.getManagerNickname())
			)
			.groupBy(todo.id, todo.title)
			.orderBy(todo.createdAt.desc());

		// 페이징 처리를 위한 총 개수 조회
		JPQLQuery<TodoSearchResponse> countQuery = queryFactory
			.select(new QTodoSearchResponse(
				todo.title,
				manager.count(),
				comment.count()
			))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(todo.comments, comment)
			.where(
				titleContains(request.getKeyword()),
				betweenCreatedAt(request.getStartDate(), request.getEndDate()),
				managerNicknameContains(request.getManagerNickname())
			)
			.groupBy(todo.id, todo.title);

		// 페이징 처리
		List<TodoSearchResponse> content = query
			.offset(request.getPage() * request.getSize())
			.limit(request.getSize())
			.fetch();

		long total = countQuery.fetchCount();

		return new PageImpl<>(content, PageRequest.of(request.getPage(), request.getSize()), total);
	}

	// 동적 쿼리 조건들
	private BooleanExpression titleContains(String keyword) {
		return StringUtils.hasText(keyword) ? QTodo.todo.title.contains(keyword) : null;
	}

	private BooleanExpression betweenCreatedAt(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		return QTodo.todo.createdAt.between(startDate, endDate);
	}

	private BooleanExpression managerNicknameContains(String nickname) {
		return StringUtils.hasText(nickname) ? QManager.manager.user.nickname.contains(nickname) : null;
	}
}
