package swyg.vitalroutes.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import swyg.vitalroutes.member.domain.Member;
import swyg.vitalroutes.participation.domain.Participation;
import swyg.vitalroutes.post.dto.BoardDTO;

import java.util.ArrayList;
import java.util.List;

// DB의 테이블 역할을 하는 클래스
@Entity // Entity로 사용하는 클래스임을 명시
@Getter
@Setter
@Table(name = "post") // DB table명, 정의한 대로 table 생성
public class BoardEntity extends BaseEntity { // boardEntity가 BaseEntity를 상속받음
    @Id // pk 컬럼 지정. 필수
    @GeneratedValue(strategy = GenerationType.IDENTITY) // mysql 기준 auto_invrement 사용
    private Long id;

    @Column(length = 20, nullable = false) // 크기 20, not null
    private String boardWriter;

    @Column(length = 24, nullable = false)
    private String boardTitle;

    @Column(length = 2000) // default 크기 255, null 가능
    private String boardContents;

    @Column(nullable = false)
    private int boardTransportation; // 이동 수단 : 걷기(0), 자전거(1)

    @Column
    private int boardHits;

    @Column
    private int fileAttached; // 파일 있으면 1, 없으면 0

    @OneToOne(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    // mappedBy : 어떤 것과 매칭을 시킬지 -> BoardFileEntity에 외래키인 boardEntity과 맞춰준다
    // boardFileEntity파일에서 매핑할 변수이름과 동일하게 작성
    private BoardFileEntity boardFileEntity;

    /**
     * 챌린지와 참여의 연관관계
     * 챌린지와 연관관계가 있어야 참여자 관련한 조회가 가능합니다. ex> 참여자 수, 내가 참여한 챌린지
     */
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Participation> participationList = new ArrayList<>();

    /**
     * 내가 작성한 챌린지 조회할 때 사용할 수 있습니다.
     * 생성하실 때 Member 객체와 연관관계를 지정해주셔야 합니다.
     */
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    */

    public static BoardEntity toSaveEntity(BoardDTO boardDTO){
        // save.html에서 입력한 값 -> boardDTO에 담긴 작성자값 -> BoardEntity의 작성자값
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardWriter(boardDTO.getBoardWriter()); // 작성자
        boardEntity.setBoardTitle(boardDTO.getBoardTitle()); // 제목
        boardEntity.setBoardContents(boardDTO.getBoardContents()); // 본문
        boardEntity.setBoardTransportation(Integer
                .parseInt(boardDTO.getBoardTransportation())); // 이동 수단 : 걷기(0), 자전거(1)
        boardEntity.setBoardHits(0); // 조회수
        boardEntity.setFileAttached(0); // 파일 존재여부 없음
        return boardEntity;
    }

    public static BoardEntity toUpdateEntity(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(boardDTO.getId()); // jpa에서 update작업임을 나타내는 id값
        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardTransportation(Integer
                .parseInt(boardDTO.getBoardTransportation()));
        boardEntity.setBoardHits(boardDTO.getBoardHits());
        return boardEntity;
    }

    public static BoardEntity toSaveFileEntity(BoardDTO boardDTO) { // service에서 파일첨부가 있는 경우 호출되는 함수
        // save.html에서 입력한 값 -> boardDTO에 담긴 작성자값 -> BoardEntity의 작성자값
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardWriter(boardDTO.getBoardWriter()); // 작성자
        boardEntity.setBoardTitle(boardDTO.getBoardTitle()); // 제목
        boardEntity.setBoardContents(boardDTO.getBoardContents()); // 본문
        boardEntity.setBoardTransportation(Integer
                .parseInt(boardDTO.getBoardTransportation())); // 이동 수단 : 걷기(0), 자전거(1)
        boardEntity.setBoardHits(0); // 조회수
        boardEntity.setFileAttached(1); // 파일 있음.
        return boardEntity;
    }
}
