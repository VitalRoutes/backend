package swyg.vitalroutes.post.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import swyg.vitalroutes.post.domain.Post;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    public Long registerPost(Post post) {
        em.persist(post);
        return post.getPostId();
    }

    public Post findById(Long postId) {
        return em.createQuery(
                        "select p from Post p " +
                                "join fetch p.member " +
                                "join fetch p.image " +
                                "where p.id = :postId", Post.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    public List<Post> findAll() {
        return em.createQuery(
                        "select p from Post p " +
                                "join fetch p.member " +
                                "join fetch p.image", Post.class)
                .getResultList();
    }

}
