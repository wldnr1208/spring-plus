package org.example.expert.domain.log.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
public class Log {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String action;     // 수행한 작업 (예: "MANAGER_SAVE_REQUEST")

	@Column(nullable = false)
	private Long todoId;       // 대상 할일 ID

	@Column(nullable = false)
	private Long requesterId;  // 요청한 사용자 ID

	@Column(nullable = false)
	private Long targetUserId; // 담당자로 지정하려는 사용자 ID

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column
	private String result;     // 결과 (성공/실패 여부 및 실패 사유)

	public Log(String action, Long todoId, Long requesterId, Long targetUserId) {
		this.action = action;
		this.todoId = todoId;
		this.requesterId = requesterId;
		this.targetUserId = targetUserId;
		this.createdAt = LocalDateTime.now();
	}

	public void setResult(String result) {
		this.result = result;
	}
}
