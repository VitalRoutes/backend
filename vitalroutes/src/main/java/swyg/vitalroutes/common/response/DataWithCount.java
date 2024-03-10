package swyg.vitalroutes.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "챌린지 참여 및 댓글 조회 시 data 에 담겨서 반환되는 데이터")
@Data
@AllArgsConstructor
public class DataWithCount<T> {
    @Schema(description = "DB 에 있는 데이터의 총 개수")
    long totalCount;

    @Schema(description = "현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지")
    boolean remainFlag; // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지

    @Schema(description = "요청한 정보들이 들어가는 필드")
    private T comments;
}
