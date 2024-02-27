package swyg.vitalroutes.hashtag.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class HashTag {
    @Id
    private String tagId; // 태그의 영문을 Key 로 사용
    private String tagName;
}
