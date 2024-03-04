package swyg.vitalroutes.participation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import swyg.vitalroutes.comments.repository.CommentRepository;
import swyg.vitalroutes.common.exception.ParticipationException;
import swyg.vitalroutes.common.response.DataWithCount;
import swyg.vitalroutes.common.utils.FileUtils;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.member.repository.MemberRepository;
import swyg.vitalroutes.participation.domain.Location;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.participation.dto.ParticipationResponseDTO;
import swyg.vitalroutes.participation.dto.ParticipationSaveDTO;
import swyg.vitalroutes.participation.repository.ParticipationRepository;
import swyg.vitalroutes.post.entity.BoardEntity;
import swyg.vitalroutes.post.repository.BoardRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static swyg.vitalroutes.common.response.ResponseType.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public DataWithCount<?> findParticipation(Long boardId, Pageable pageable) {
        List<Participation> entityList = participationRepository.findAllByBoardId(boardId, pageable);
        List<ParticipationResponseDTO> dtoList = entityList.stream().map(ParticipationResponseDTO::new).toList();

        for (ParticipationResponseDTO dto : dtoList) {
            long size = commentRepository.countByParticipationId(dto.getParticipationId());
            dto.setTotalComments(size);
        }

        long count = participationRepository.countAllByBoardId(boardId);

        boolean remainFlag = (pageable.getOffset() * (pageable.getPageNumber() + 1)) < count;  // 현재까지 보여지고 있는 데이터 외에 남은 데이터가 있는지

        return new DataWithCount<>(count, remainFlag, dtoList);
    }


    public void saveParticipation(ParticipationSaveDTO saveDTO) {
        List<Location> locations = new ArrayList<>();
        List<MultipartFile> files = saveDTO.getFiles();
        int seq = 0;
        for (MultipartFile file : files) {
            String fileName = "file image url";
            // String fileName = s3UploadService.saveFile(file);
            double[] locationInfo = FileUtils.getLocationInfo(file);
            locations.add(Location.createLocation(++seq, fileName, locationInfo));
        }

        Member member = memberRepository.findById(saveDTO.getMemberId())
                .orElseThrow(() -> new ParticipationException(BAD_REQUEST, FAIL, "사용자가 존재하지 않습니다"));
        BoardEntity board = boardRepository.findById(saveDTO.getBoardId())
                .orElseThrow(() -> new ParticipationException(BAD_REQUEST, FAIL, "게시글이 존재하지 않습니다"));


        /**
         * 여기서 Board 의 Location 과 비교 필요
         */

        Participation participation = Participation.createParticipation(saveDTO.getContent(), member, board, locations);
        participationRepository.save(participation);
    }

    public void deleteParticipation(Long participationId) {
        Participation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationException(BAD_REQUEST, FAIL, "참여 게시글이 존재하지 않습니다"));
        participationRepository.deleteById(participationId);
    }
}
