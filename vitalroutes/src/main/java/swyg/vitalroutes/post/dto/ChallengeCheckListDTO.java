package swyg.vitalroutes.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Challenge 목록 조회 시 사용")
public class ChallengeCheckListDTO {

    private Long boardId;

    @Schema(description = "Challenge 제목을 담는 변수")
    private String challengeTitle; // 제목

    @Schema(description = "Challenge 저장된 대표사진 이름을 담는 변수")
    private String storedTitleImageName; // 서버 저장용 파일 이름

    @Schema(description = "Challenge 참여수를 담는 변수")
    private int boardParty;
}
