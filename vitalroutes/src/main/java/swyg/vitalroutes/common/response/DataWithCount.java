package swyg.vitalroutes.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataWithCount<T> {
    long totalCount;
    boolean remainFlag; // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지
    private T data;
}
