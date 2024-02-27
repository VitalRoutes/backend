package swyg.vitalroutes.hashtag.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swyg.vitalroutes.hashtag.domain.HashTag;

@Repository
@RequiredArgsConstructor
public class HashTagRepository {

    private final EntityManager em;

    public HashTag findTag(String tagId) {
        return em.find(HashTag.class, tagId);
    }
}
