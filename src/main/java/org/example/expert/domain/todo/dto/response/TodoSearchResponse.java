package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoSearchResponse {
	private String title;           // 일정 제목
	private Long managerCount;      // 담당자 수
	private Long commentCount;      // 댓글 수

	@QueryProjection
	public TodoSearchResponse(String title, Long managerCount, Long commentCount) {
		this.title = title;
		this.managerCount = managerCount;
		this.commentCount = commentCount;
	}
}
