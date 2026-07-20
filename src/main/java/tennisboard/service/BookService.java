package tennisboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tennisboard.entity.AuthorEntity;
import tennisboard.entity.BookEntity;
import tennisboard.repository.AuthorDao;
import tennisboard.repository.BookDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookDao bookDao;

    private final AuthorDao authorDao;

    public BookService(BookDao bookDao, AuthorDao authorDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
    }

    @Transactional
    public BookEntity saveBookNaive(String authorName, String bookName) {
        AuthorEntity authorEntity = authorDao.saveAuthor(new AuthorEntity(authorName));

        BookEntity bookEntity = bookDao.saveBook(new BookEntity(bookName, authorEntity));

        return bookEntity;
        //здесь по идее нужно возвращать именно книгу (т.е. должен быть маппинг энтити в дто)
    }

    @Transactional
    public List<BookEntity> saveBooksForOneAuthorNaive(String authorName, List<String> bookNames) {
        AuthorEntity author = authorDao.saveAuthor(new AuthorEntity(authorName));

        List<BookEntity> books = new ArrayList<>();
        for (String bookName : bookNames) {
            BookEntity book = bookDao.saveBook(new BookEntity(bookName, author));
            books.add(book);
        }

        return books;
        //ВСЕГДА создаст нового автора (и новые же книги) - новые строки!
        //-- т.е. вообще говоря сначала надо бы проверять
        // (т.к. автор - ункиальный, без проверки выбьет ошибку на попытке вставки)
    }

    //здесь мы проверяем, есть ли автор и если нет - создаем (нужно имя) - сомнительная хрень, хотя и возможная
    //хотя, сомнительно потому что уже как будто мы в 1 методе делаем... многое
    //скорее разделять: saveBooksForOnlyExistAuthor(authorId, bookNames) ((автор уже должен быть))
    // saveBooksForAuthorByName(authorName, bookNames) ((“найди по имени или создай”.))

    @Transactional
    public List<BookEntity> saveBooksForAnyAuthor(
            Long authorId,
            String authorName,
            List<String> bookNames) {
        Optional<AuthorEntity> author = authorDao.findAuthor(authorId);

        AuthorEntity currentAuthor;
        if (author.isPresent()) {
            currentAuthor = author.get();
        } else {
            currentAuthor = authorDao.saveAuthor(new AuthorEntity(authorName));
        }

        List<BookEntity> books = new ArrayList<>();
        for (String bookName : bookNames) {
            BookEntity book = bookDao.saveBook(new BookEntity(bookName, currentAuthor));
            books.add(book);
        }

        return books;
    }


    //здесь мы проверяем, есть ли автор и если нет - создаем (нужно имя)
    //хотя, сомнительно потому что уже как будто мы в 1 методе делаем... многое
    @Transactional
    public List<BookEntity> saveBooksForOnlyExistAuthor(
            Long authorId,
            List<String> bookNames) {
        Optional<AuthorEntity> author = authorDao.findAuthor(authorId);

        AuthorEntity currentAuthor;
        if (author.isPresent()) {
            currentAuthor = author.get();
        } else {
            throw new IllegalArgumentException("Authot should exist beforehand");
        }

        List<BookEntity> books = new ArrayList<>();
        for (String bookName : bookNames) {
            BookEntity book = bookDao.saveBook(new BookEntity(bookName, currentAuthor));
            books.add(book);
        }

        return books;
    }

    // т.е. фамилия - уникальная - здесь вернется 1 ответ на автора.
    // +1 запрос на выдачу ВСЕХ книг этого автора - т.к. лист (2 запроса)
    // - см ниже.

    // тут МОЖЕТ быть Н+1, если автор не уникальный и ищется например по маске (лев толстой, марк толстой и пр.)
    // теперь. что будет если не уникальная ? он найдет например 1000 толстых (это ТААКЖЕ 1 запрос, как и раньше)
    // и потом на каждого из них сделает еще 1 запрос (это еще 1000 запросов, но каждого толстого - куча мелких запросов)
    // теперь. пришло 1000 пользоваелей. для каждого польователя это 1+1000 запросов (1001)
    // стало 1001 * 100 = 100 100 запросов к БД
    // КАЖДЫЙ запрос - отдельный геморрой для БД, как бы ушстро она не работала...
    // сравни с вариантом, когда 1000 пользователей делают ровно 1 запрос (тот самый джойн != ДЖОЙНФЕЧ)

    public List<BookEntity> findBooksByAuthorQuestionable(String authorName) {
        Optional<AuthorEntity> author = authorDao.findAuthorByName(authorName); //1 запрос

        if (author.isEmpty()) {
            return List.of();
        }

        return bookDao.findBooksByAuthorId(author.get().getId());
    }

    //1 запрос, без проблемы Н+1 (в-общем виде)

    public List<BookEntity> findBooksByAuthorProper(String authorName) {
        return bookDao.findBooksByAuthorName(authorName);
    }

    public void printAllAuthorsAndBooks() {
        List<BookEntity> allBooks = bookDao.findAllAuthosAndAllBooks();

        for (BookEntity allBook : allBooks) {
            System.out.println("author " + allBook.getAuthor()
                    + ", title " + allBook.getTitle()
                    + ", id " + allBook.getId());
        }
    }
}
