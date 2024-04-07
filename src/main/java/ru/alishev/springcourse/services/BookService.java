package ru.alishev.springcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.repositories.BooksRepository;
import ru.alishev.springcourse.repositories.PeopleRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BooksRepository booksRepository;

    private final PeopleRepository peopleRepository;

    @Autowired
    public BookService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }

    public List<Book> findByOwnerId(int id){
        Person owner = peopleRepository.findById(id).orElse(null);
        return booksRepository.findByOwner(owner);
    }

    public List<Book> findAll(){
        return booksRepository.findAll();
    }

    public List<Book> findAll(int page, int booksPerPage, boolean sortByYear){
        Sort sort = sortByYear ? Sort.by("year") : Sort.unsorted();
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, sort)).getContent();
    }

    public Book findOne(int id){
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }

    public Optional<Person> findOwnerById(int id){
        Book book = findOne(id);
        if(book != null) {
            Person owner = book.getOwner();
            return Optional.ofNullable(owner);
        } else
            return Optional.empty();
    }

    @Transactional
    public void save(Book book){
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook){// Объект пришел с формы, поэтому нужно сделать save этого объекта
        Book bookToBeUpdated = booksRepository.findById(id).get();

        //Добавляем по сути новую книгу (которая не находится в Persistence context), поэтому нужен save()
        updatedBook.setId(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner());// чтобы не терялась связь при обновлении

        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }

    @Transactional
    public void assign(int id, Person selectedPerson){
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(LocalDateTime.now());
                });
    }

    @Transactional
    public void release(int id){
        // Книга находится в persistence context. Поэтому вызывать booksRepository.save(book); не нужно
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                }
        );
    }

    public List<Book> findByTitleStartingWith(String startingWith){
        return booksRepository.findByTitleStartingWith(startingWith);
    }
}



























