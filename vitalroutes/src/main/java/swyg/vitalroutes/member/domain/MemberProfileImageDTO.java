package swyg.vitalroutes.member.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemberProfileImageDTO {
    private MultipartFile profileImage;
    private String profileImageURL;
}
