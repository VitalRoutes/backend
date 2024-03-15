package swyg.vitalroutes.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import swyg.vitalroutes.post.entity.BoardEntity;
import swyg.vitalroutes.post.entity.TagEntity;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
