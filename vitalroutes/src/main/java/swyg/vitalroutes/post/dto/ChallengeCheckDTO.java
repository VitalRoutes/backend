package swyg.vitalroutes.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Challenge 조회 시 사용")
public class ChallengeCheckDTO {
    @Schema(description = "Challenge가 저장된 DB id key값")
    private Long id;
    @Schema(description = "Challenge 작성자명을 담는 변수")
    private String challengeWriter; // 작성자
    @Schema(description = "Challenge 제목을 담는 변수")
    private String challengeTitle; // 제목
    @Schema(description = "Challenge 본문 내용을 담는 변수")
    private String challengeContents; // 본문
    @Schema(description = "Challenge 등록시 사용한 이동 수단을 담는 변수. ex. 걷기(0), 자전거(1) ")
    private String challengeTransportation; // 이동수단

    @Schema(description = "챌린지 조회 수")
    private int boardHits;  // 조회수 
    @Schema(description = "Challenge 참여자 수")
    private int totalComments;  // 참가자 수, 댓글 수

    @Schema(description = "Challenge 태그 문자열 담는 변수")
    private List<String> tags;

    @Schema(description = "Challenge 경로 사진이 저장 플래그")
    private int existingMode; // 파일 첨부 여부(첨부 1, 미첨부 0)

    @Schema(description = "Challenge 저장된 대표사진 이름을 담는 변수")
    private String storedTitleImageName; // 서버 저장용 파일 이름

    @Schema(description = "Challenge 저장할 출발지 사진 파일명을 담는 변수")
    private String storedStartingPositionImageName; // 출발지 이미지 서버저장용 파일 이름
    @Schema(description = "출발지 위도")
    private double startingPosLat; // 출발지 위도
    @Schema(description = "출발지 경도")
    private double startingPosLon; // 출발지 경도
    @Schema(description = "Challenge 저장할 도착지 사진 파일명을 담는 변수")
    private String storedDestinationImageName; // 도착지 이미지 서버저장용 파일 이름
    @Schema(description = "도착지 위도")
    private double destinationLat;
    @Schema(description = "도착지 경도")
    private double destinationLon;
    @Schema(description = "Challenge 저장할 경유지1 사진 파일명을 담는 변수")
    private String storedStopOverImage1Name; // 경유지1 이미지 서버저장용 파일 이름
    @Schema(description = "경유지1 위도")
    private double stopOver1Lat;
    @Schema(description = "경유지1 경도")
    private double stopOver1Lon;
    @Schema(description = "Challenge 저장할 경유지2 사진 파일명을 담는 변수")
    private String storedStopOverImage2Name; // 경유지2 이미지 서버저장용 파일 이름
    @Schema(description = "경유지2 위도")
    private double stopOver2Lat;
    @Schema(description = "경유지2 경도")
    private double stopOver2Lon;
    @Schema(description = "Challenge 저장할 경유지3 사진 파일명을 담는 변수")
    private String storedStopOverImage3Name; // 경유지3 이미지 서버저장용 파일 이름
    @Schema(description = "경유지3 위도")
    private double stopOver3Lat;
    @Schema(description = "경유지3 경도")
    private double stopOver3Lon;
}
