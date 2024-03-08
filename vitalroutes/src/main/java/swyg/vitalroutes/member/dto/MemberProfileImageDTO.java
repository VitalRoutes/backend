package swyg.vitalroutes.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "프로필 이미지를 수정할 때 사용, form-data 형식으로 전송 필요")
@Data
public class MemberProfileImageDTO {
    @Schema(description = "S3에 프로필 이미지를 업로드할 때 사용하는 필드")
    private MultipartFile profileImage;

    @Schema(description = "업로드 이후 전달 받은 URL 로 프로필 이미지를 변경할 때 URL 을 담는 필드")
    private String profileImageURL;
}
