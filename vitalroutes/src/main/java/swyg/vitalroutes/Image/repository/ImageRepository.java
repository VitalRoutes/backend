package swyg.vitalroutes.Image.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swyg.vitalroutes.Image.domain.Image;

@Repository
@RequiredArgsConstructor
public class ImageRepository {
    private final EntityManager em;

    public Image findImageByPostId(Long postId) {
        return em.createQuery("select i from Image i where i.post.id = :postId", Image.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }
}
