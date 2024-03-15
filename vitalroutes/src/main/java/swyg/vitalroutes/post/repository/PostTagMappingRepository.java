package swyg.vitalroutes.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyg.vitalroutes.post.entity.BoardTagMapping;
import swyg.vitalroutes.post.entity.TagEntity;

public interface PostTagMappingRepository extends JpaRepository<BoardTagMapping, Long> {
}
