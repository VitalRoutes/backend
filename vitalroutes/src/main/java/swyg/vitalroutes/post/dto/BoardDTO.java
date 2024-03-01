package swyg.vitalroutes.post.dto;

import lombok.*;

import java.time.LocalDateTime;

// DTO ( Data Transfer Object), VL, Bean, ... 비슷한 용도
// Entity는 살짝 결이 다름 ( 여러 파라미터를 한 객체에 담아서 보내는 용도 )
@Getter
@Setter
@ToString
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class BoardDTO {
    private Long id;
    private String boardWriter;
    private String boardTitle;
    private String boardContents;
    private int boardHits; // 조회수
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;


}
