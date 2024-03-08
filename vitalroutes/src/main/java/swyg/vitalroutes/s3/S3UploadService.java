package swyg.vitalroutes.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;
    private String savedChallengeTitleImagePath = "vital_routes_src/challenge_title_image/";
    private String savedChallengePathImagePath = "vital_routes_src/path_image/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(UUID.randomUUID());
        sb.append("-");
        sb.append(multipartFile.getOriginalFilename());
        String originalFilename = sb.toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    public ResponseEntity<UrlResource> downloadImage(String originalFilename) {
        UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket, originalFilename));

        String contentDisposition = "attachment; filename=\"" +  originalFilename + "\"";

        // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);

    }

    public String saveChallengeTitleImage(MultipartFile multipartFile) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(savedChallengeTitleImagePath);
        sb.append(UUID.randomUUID());
        sb.append("-");
        sb.append(multipartFile.getOriginalFilename());
        String originalFilename = sb.toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    public String saveChallengePathImage(MultipartFile multipartFile) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(savedChallengePathImagePath);
        sb.append(UUID.randomUUID());
        sb.append("-");
        sb.append(multipartFile.getOriginalFilename());
        String originalFilename = sb.toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFilename).toString();
    }

    public S3Object getFileURI(String savePath) {
        S3Object s3Object = amazonS3.getObject(bucket, savePath);
        //S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        return  s3Object;
    }
}
