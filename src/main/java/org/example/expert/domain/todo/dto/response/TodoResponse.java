package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserResponse user;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public TodoResponse(Long id, String title, String contents, String weather, UserResponse user, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Getter
    @Setter  // setPage, setSize 등을 사용하기 위해 필요
    @NoArgsConstructor
    public static class TodoSearchCondition {
        private int page;
        private int size;
        private String weather;
        private LocalDateTime createdAt; // 변경: LocalDateTime → String
        private LocalDateTime modifiedAt;

        public TodoSearchCondition(int page, int size, String weather, LocalDateTime createdAt, LocalDateTime modifiedAt) {
            this.page = page;
            this.size = size;
            this.weather = weather;
            this.createdAt = createdAt;
            this.modifiedAt = modifiedAt;
        }
    }
}
