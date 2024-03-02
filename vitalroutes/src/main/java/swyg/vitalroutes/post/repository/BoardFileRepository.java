package swyg.vitalroutes.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyg.vitalroutes.post.entity.BoardFileEntity;

public interface BoardFileRepository extends JpaRepository<BoardFileEntity, Long> {
}
