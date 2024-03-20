package swyg.vitalroutes.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Challenge 생성 시 사용")
public class ChallengeSaveFormDTO {
    @Id // pk 컬럼 지정. 필수
    @GeneratedValue(strategy = GenerationType.IDENTITY) // mysql 기준 auto_invrement 사용
    private Long id;

    @Schema(description = "Challenge 작성자명을 담는 변수")
    private String challengeWriter; // 작성자
    @Schema(description = "Challenge 제목을 담는 변수")
    private String challengeTitle; // 제목
    @Schema(description = "Challenge 본문 내용을 담는 변수")
    private String challengeContents; // 본문
    @Schema(description = "Challenge 등록시 사용한 이동 수단을 담는 변수. ex. 걷기(0), 자전거(1) ")
    private String challengeTransportation; // 이동수단

    @Schema(description = "Challenge 태그 문자열 담는 변수")
    private List<String> tags;

    @Schema(description = "Challenge 대표사진 파일을 담는 변수")
    private MultipartFile titleImage; // 실제 파일을 담아줄 수 있는 역할 (대표사진 저장)

    @Schema(description = "Challenge 이동 경로 사진 중 출발지 사진 파일을 담는 변수")
    private MultipartFile startingPositionImage; // 출발지 이미지 파일 담는용도
    @Schema(description = "Challenge 이동 경로 사진 중 출발지 위도")
    private double startingPosLat;
    @Schema(description = "Challenge 이동 경로 사진 중 출발지 경도")
    private double startingPosLon;
    
    @Schema(description = "Challenge 이동 경로 사진 중 도착지 사진 파일을 담는 변수")
    private MultipartFile destinationImage; // 도착지 이미지 파일 담는용도
    @Schema(description = "Challenge 이동 경로 사진 중 도착지 위도")
    private double destinationLat;
    @Schema(description = "Challenge 이동 경로 사진 중 도착지 경도")
    private double destinationLon;

    @Schema(description = "Challenge 이동 경로 사진 중 경유지1 사진 파일을 담는 변수")
    private MultipartFile stopOverImage1; // 경유지1 이미지 파일 담는용도
    @Schema(description = "Challenge 이동 경로 사진 중 경유지1 위도")
    private double stopOver1Lat;
    @Schema(description = "Challenge 이동 경로 사진 중 경유지1 경도")
    private double stopOver1Lon;

    @Schema(description = "Challenge 이동 경로 사진 중 경유지2 사진 파일을 담는 변수")
    private MultipartFile stopOverImage2; // 경유지2 이미지 파일 담는용도
    @Schema(description = "Challenge 이동 경로 사진 중 경유지2 위도")
    private double stopOver2Lat;
    @Schema(description = "Challenge 이동 경로 사진 중 경유지2 경도")
    private double stopOver2Lon;

    @Schema(description = "Challenge 이동 경로 사진 중 경유지3 사진 파일을 담는 변수")
    private MultipartFile stopOverImage3; // 경유지3 이미지 파일 담는용도
    @Schema(description = "Challenge 이동 경로 사진 중 경유지3 위도")
    private double stopOver3Lat;
    @Schema(description = "Challenge 이동 경로 사진 중 경유지3 경도")
    private double stopOver3Lon;
}
