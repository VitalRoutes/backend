package swyg.vitalroutes.common.response;

import lombok.Data;

@Data
public class MyChallengeDTO {
    private Long boardId;
    private String title;

    public MyChallengeDTO(Long boardId, String title) {
        this.boardId = boardId;
        this.title = title;
    }
}
