package swyg.vitalroutes.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyg.vitalroutes.post.entity.BoardFileEntity;
import swyg.vitalroutes.post.entity.BoardPathImageEntity;

public interface BoardPathImageRepository extends JpaRepository<BoardPathImageEntity, Long> {
}
