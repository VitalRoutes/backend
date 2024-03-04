package swyg.vitalroutes.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Getter
@Setter
@Table(name = "path_image")
public class BoardPathImageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @Column
    private String originalFileName; // 경로사진 이름

    @Column
    private String storedFileName; // 저장된 경로사진 이름
     */
    @Column
    private double latitude; // 위도

    @Column
    private double lognitude; // 경도

    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계, 부모가 호출되더라도 자식을 쓰고자 한다면 값을 불러오고, 아니라면 불러오지 않음
    //@ManyToOne(fetch = FetchType.EAGER) // N:1 관계, 요청을 하든 안하든 부모가 호출되면 자식도 호출되어 모든 값을 불러옴
    @JoinColumn(name = "image_id") // table에 만들어지는 column이름을 정한다.
    private BoardFileEntity boardFileEntity; // DB에 BIGINT타입이지만 Long이 아닌 부모entity타입으로 입력해야한다. 실제 DB에는 그냥 ID값만 들어가게된다.

    public static BoardPathImageEntity toBoardPathImageEntity(BoardFileEntity boardFileEntity,
                                                              double latitude,
                                                              double lognitude) {
        BoardPathImageEntity boardPathImageEntity = new BoardPathImageEntity();
        boardPathImageEntity.setLatitude(latitude);
        boardPathImageEntity.setLognitude(lognitude);
        boardPathImageEntity.setBoardFileEntity(boardFileEntity);
        return boardPathImageEntity;
    }
}
