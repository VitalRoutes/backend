package swyg.vitalroutes.common.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.common.exception.FileProcessException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
public class FileUtils {
    public static File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        log.info("originalFilename = {}", originalFilename);
        log.info("contentType = {}", contentType);

        if (!contentType.startsWith("image")) {
            throw new FileProcessException(BAD_REQUEST, FAIL, "이미지 파일만 업로드 가능합니다");
        }

        File convFile = new File(originalFilename);
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

    public static double[] getLocationInfo(MultipartFile multipartFile) {
        double latitude = 0.0;
        double longitude = 0.0;
        try {
            File file = multipartFileToFile(multipartFile);
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

            if (gpsDirectory == null || gpsDirectory.isEmpty()) {
                throw new FileProcessException(BAD_REQUEST, FAIL, "파일에 GPS 정보가 없습니다");
            }

            if (gpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE) && gpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE)) {
                latitude = gpsDirectory.getGeoLocation().getLatitude(); // 위도
                longitude = gpsDirectory.getGeoLocation().getLongitude();    // 경도
            }

            if (file.exists()) {
                if (file.delete()) {
                    log.info("파일 삭제 성공");
                } else {
                    log.info("파일 삭제 실패");
                }
            }

            log.info("latitude = {}", latitude);
            log.info("longitude = {}", longitude);
        } catch (IOException e) {
            throw new FileProcessException(INTERNAL_SERVER_ERROR, ERROR, "파일 변환에 실패하였습니다");
        } catch (ImageProcessingException e) {
            throw new FileProcessException(INTERNAL_SERVER_ERROR, ERROR, "이미지 정보를 읽을 수 없습니다");
        }
        return new double[]{latitude, longitude};
    }

    public static double calDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double theta = longitude1 - longitude2;
        double dist = Math.sin(deg2rad(latitude1)) * Math.sin(deg2rad(latitude2))
                + Math.cos(deg2rad(latitude1)) * Math.cos(deg2rad(latitude2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1609.344;

        log.info("dist = {}", dist);

        return dist;
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
