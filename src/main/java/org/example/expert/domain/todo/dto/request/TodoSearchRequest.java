package org.example.expert.domain.todo.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // Setter 추가
@NoArgsConstructor
public class TodoSearchRequest {
	private String keyword;         // 제목 검색 키워드
	private LocalDateTime startDate;// 검색 시작일
	private LocalDateTime endDate;  // 검색 종료일
	private String managerNickname; // 담당자 닉네임
	private int page = 0;          // 페이지 번호
	private int size = 10;         // 페이지 크기
}