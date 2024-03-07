package swyg.vitalroutes.participation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.common.utils.FileUtils;
import swyg.vitalroutes.hide.repository.HideRepository;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.ParticipationImage;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.dto.*;
import swyg.vitalroutes.participation.repository.ParticipationRepository;
import swyg.vitalroutes.post.entity.BoardEntity;
import swyg.vitalroutes.post.repository.BoardRepository;
import swyg.vitalroutes.s3.S3UploadService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final HideRepository hideRepository;
    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final S3UploadService s3UploadService;


    public DataWithCount<?> findParticipation(Long memberId, Long boardId, Pageable pageable) {
        // 숨김처리한 참여 게시글의 ID 를 조회
        List<Long> hidedParticipations = hideRepository.findHidedParticipations(memberId);
        List<Long> hidedComments = hideRepository.findHidedComments(memberId);
        
        // 숨김처리한 게시글 혹은 댓글이 없으면 NULL 이 들어가서 아무것도 조회되지 않음
        if (hidedParticipations.size() == 0) {
            hidedParticipations.add(-1L);
        }
        
        if (hidedComments.size() == 0) {
            hidedComments.add(-1L);
        }

        // 참여 게시글 조회
        Page<Participation> pagingData = participationRepository.findAllByBoardId(boardId, hidedParticipations, pageable);
        List<ParticipationResponseDTO> dtoList = pagingData.map(ParticipationResponseDTO::new).toList();

        for (ParticipationResponseDTO dto : dtoList) {
            long size = commentRepository.countByParticipationId(dto.getParticipationId(), hidedComments);
            dto.setTotalComments(size);
        }

        long count = pagingData.getTotalElements(); // 총 데이터의 수
        boolean remainFlag = pagingData.hasNext();  // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지

        return new DataWithCount<>(count, remainFlag, dtoList);
    }


    public void saveParticipation(ParticipationSaveDTO saveDTO) {
        List<ParticipationImage> participationImages = new ArrayList<>();
        List<MultipartFile> files = saveDTO.getFiles();
        int seq = 0;

        /**
         * 아래 반복문 안에서 Board 의 Location 과 비교 필요
         */
        
        for (MultipartFile file : files) {
            String fileName = "file image url";
            // String fileName = s3UploadService.saveFile(file);
            double[] locationInfo = FileUtils.getLocationInfo(file);
            participationImages.add(ParticipationImage.createParticipationImage(++seq, fileName));
        }

        Member member = memberRepository.findById(saveDTO.getMemberId())
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "사용자가 존재하지 않습니다"));
        BoardEntity board = boardRepository.findById(saveDTO.getBoardId())
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "게시글이 존재하지 않습니다"));


        Participation participation = Participation.createParticipation(saveDTO.getContent(), member, board, participationImages);
        participationRepository.save(participation);
    }

    public void deleteParticipation(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        participationRepository.deleteById(participationId);
    }

    public ParticipationResponseDTO findById(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        return new ParticipationResponseDTO(participation);
    }

    /**
     * 이미지 변경 2가지 선택지
     * 1. 변경할 이미지를 업로드한다
     * 2. 변경할 이미지 없이 내용만 변경된다면 내용을 변경하는 API 를 호출한다
     */
    public ImageResponseDTO uploadImage(ImageSaveDTO imageDTO) {
        String url = "modifyURL";
        MultipartFile file = imageDTO.getFile();
        double[] locationInfo = FileUtils.getLocationInfo(file);

        /**
         * 여기서 Board 이미지의 위치정보와 비교
         * 위치정보가 유사하다면 OK
         */
        Long boardId = imageDTO.getBoardId();
        int sequence = imageDTO.getSequence();

        /*
        try {
            url = s3UploadService.saveFile(file);
        } catch (IOException exception) {
            throw new ParticipationException(INTERNAL_SERVER_ERROR, FAIL, "파일 업로드 중 에러가 발생하였습니다");
        }
        */
        return new ImageResponseDTO(sequence, url);
    }

    public void modifyParticipation(Long participationId, ParticipationModifyDTO modifyDTO) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(NOT_FOUND, FAIL, "참여 게시글이 존재하지 않습니다"));
        participation.setContent(modifyDTO.getContent());
        // 파일 변경

        List<ParticipationImage> newImages = modifyDTO.getUploadedFiles().stream()
                .map(imageResponseDTO -> ParticipationImage
                        .createParticipationImage(imageResponseDTO.getSequence(), imageResponseDTO.getFileName()))
                .toList();
        participation.getParticipationImages().clear();
        for (ParticipationImage newImage : newImages) {
            participation.getParticipationImages().add(newImage);
        }
    }
}
