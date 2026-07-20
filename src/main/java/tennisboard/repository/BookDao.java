package tennisboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import tennisboard.entity.AuthorEntity;
import tennisboard.entity.BookEntity;

import java.util.List;
import java.util.Optional;

@Repository
public class BookDao {
    @PersistenceContext
    private EntityManager em;

    public BookEntity saveBook(BookEntity book) {
        em.persist(book);
        return book;
    }

    //все файндБай - по-хорошему опшионал (т.к. сценарий "не нашел" - реален), далее упрощаю, здесь "как верно"
    //нюанс: getSingleResu() - если НЕ найдет, выкинет эксепшен, потому либо обрабатывать так:

//        try {
//        BookEntity book = em.createQuery(query, BookEntity.class)
//                .setParameter("author_id", authorId)
//                .setParameter("title", bookName)
//                .getSingleResult();
//        return Optional.of(book); // Если нашли — красиво упаковываем
//    } catch (
//    NoResultException e) {
//        return Optional.empty();  // Если не нашли — возвращаем пустой Optional без всяких падений!
//    }

    //либо через стримы, элегантно (под капотом резалсет не выкинет исключения):
    //getResultList - может вернуть 0 или 1 элемент
    //ВАЖНО: при этом, findFirst - вернет первую попавшуюся книгу, потому по-хорошему должен быть констрнейнт на книги
    // книга = уникальна, ЛИБО если его нет, а резалсет вернул больше 1 книги - это тоже ошибка, и ее нужно обрабатывать
    //Returns an Optional describing the first element of this stream, or an empty Optional if the stream is empty
    public Optional<BookEntity> findBookByAuthor(String bookName, Long authorId) {
        String query = """
                select b
                from BookEntity b
                where b.author.id = :author_id
                  and b.title = :title
                """;
        return em.createQuery(query, BookEntity.class)
                .setParameter("author_id", authorId)
                .setParameter("title", bookName)
                .getResultList()
                .stream()
                .findFirst();
    }

    //JPQL: Entity + Java-поля !!!!! -- смотри ЭНТИТИ, не таблицы
    // т.е. как у объекта получить его поле ? по полю.
    // а поле поля ? по . тоже.

    //"автор есть, но книг нет" - возможно

    public List<BookEntity> findBooksByAuthorId(Long authorId) {
        String query = """
                select b
                from BookEntity b
                where b.author.id = :author_id
                """;
        return em.createQuery(query, BookEntity.class)
                .setParameter("author_id", authorId)
                .getResultList();
        //лучше через ЕМ, но только если есть обратная связь ?
    }

    //!!! все зависит от запроса (контролер) - если там айди есть, причем конкретной сущности - отлично.
    //например, найти книгу конкретного автора, и есть айди и автора и книги - тогда 2 запроса.
    //или можно одним (куери)

    //теперь, например запрос: найти все книги автора. автора-то мы получим...
    // а дальше - если одна направленность (книги - автор), книги не сможем (нужно чтобы была двойная)

    //другое дело, если задача просто найти книгу. ну, берешь и ищешь.

    //айди есть далеко не всегда - т.к. айди создает БД, обычно это кастомные зпросы
    //НО. при этом em.find() работает напрямую с кешом, потом - да, он как раз в приоритете если овтечает запросу
    //в-общем целом, если у нас тут есть хотя бы 1 имя - это запрос. т.е. имя + айди. айди + имя иил 2 имени.

    //!!!!!!!!!!!!!!НЕОДНОЗНАЧНАЯ ШТУКА!!!!!!!!!!!! т.е. может быть вариант: автора не нашли - с одной стороны
    //см. сервис
    // а с другой - "автор есть, но книг нет" - оба будут давать пустой список

    @Deprecated
    public List<BookEntity> findBooksByExistingAuthor(Long authorId) {
        AuthorEntity author = em.find(AuthorEntity.class, authorId);
        if (author == null) {
            return List.of();
        }

        return findBooksByAuthorId(authorId);
    }

    /*
            String query = """
                select b
                from books b
                where b.author.id = :authorId
                and b.
                """;
                //а Б - что ? а у книги есть - придется делать джойн, чтобы вытащить автора по его имени
     */

    /*
            String query = """
                select b
                from BookEntity b
                join AuthorEntity a on b.author.id = a.id
                where a.name = :authorName
                and b.title = :title
                """;
                //так нормально, НО только с точки зрения скуэля
                //т.к. жпкуэль УЖЕ имеет ссылку на автора!

                //ЛУЧШЕ:
        String query = """
                select b
                from BookEntity b
                join b.author a
                where a.name = :authorName
                and b.title = :title
                """;
     */

    //т.е. НЕ НУЖНО ЯВНО связывать 2 таблицы джойнами!
    //в этом плане, я УЖЕ могу из таблицы книг перейти в автора (джойн, что 1, что 2 - может ыбыть, но лишний).
    //можно через объект книги - в нем поле автор - перейти к полям автора
    //!!! НЮАНС: под капотом ЖПА 1 хрен делает джойн, так что лучше делать экспелисит
    public List<BookEntity> findBookByNameByAuthorNameImplicit(String authorName, String bookName) {
        String query = """
                select b
                from BookEntity b
                where b.author.name = :authorName
                and b.title = :title
                """;
        return em.createQuery(query, BookEntity.class)
                .setParameter("authorName", authorName)
                .setParameter("title", bookName)
                .getResultList();
    }

    //так обычно лучше. потому как эксплисит и ближе к скуэль-синтаксису + сразу понятно откуда вообще исходит и е нужно
    //думать о магии...
    public List<BookEntity> findBookByNameByAuthorNameExplicit(String authorName, String bookName) {
        String query = """
                select b
                from BookEntity b
                join b.author a
                where a.name = :authorName
                and b.title = :title
                """;
        return em.createQuery(query, BookEntity.class)
                .setParameter("authorName", authorName)
                .setParameter("title", bookName)
                .getResultList();
    }

    public List<BookEntity> findBooksByAuthorName(String authorName) {
        String query = """
                select b
                from BookEntity b
                join b.author a
                where a.name = :authorName
                """;
        return em.createQuery(query, BookEntity.class)
                .setParameter("authorName", authorName)
                .getResultList();
    }

    //select b, a - при этом ЖПА сделает странную конструкцию а-ля массив массивов
    //при обычном джойне буудт только пересечеения (не страшно если обязательно есть привязка)
    //а вот при лефт джойне к книгам присоединим второв (условно у книги может не был авторов, на то и лефт джойн)

    public List<BookEntity> findAllAuthosAndAllBooks() {
        String query = """
                select b
                from BookEntity b
                join b.author a
                """;
        return em.createQuery(query, BookEntity.class)
                .getResultList();
    }
}
