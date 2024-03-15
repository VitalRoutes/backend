package swyg.vitalroutes.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import swyg.vitalroutes.post.entity.BoardTagMapping;
import swyg.vitalroutes.post.entity.TagEntity;

import java.util.Optional;

public interface PostTagMappingRepository extends JpaRepository<BoardTagMapping, Long> {
}
