package swyg.vitalroutes.participation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ParticipationImage {
    private int sequence;
    private String fileName;
    // 챌린지 참여에 대한 이미지는 위치정보를 저장할 필요가 없음

    public static ParticipationImage createParticipationImage(int seq, String fileName) {
        ParticipationImage participationImage = new ParticipationImage();
        participationImage.setSequence(seq);
        participationImage.setFileName(fileName);
        return participationImage;
    }
}
