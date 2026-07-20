package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennisboard.entity.AuthorEntity;
import tennisboard.entity.BookEntity;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDao {

    @PersistenceContext
    EntityManager em;

    public AuthorEntity saveAuthor(AuthorEntity author) {
        em.persist(author);
        return author;
    }

    public Optional<AuthorEntity> findAuthor(Long authorId) {
        return Optional.ofNullable(em.find(AuthorEntity.class, authorId));
    }

    //здесь точно ОК, т.к. автор - уникален

    public Optional<AuthorEntity> findAuthorByName(String authorName) {
        String query = """
                select a
                from AuthorEntity a
                where a.name = :name
                """;
        return em.createQuery(query, AuthorEntity.class)
                .setParameter("name", authorName)
                .getResultList()
                .stream()
                .findFirst();
    }
}
