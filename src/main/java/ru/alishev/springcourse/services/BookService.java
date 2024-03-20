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
    public void update(int id, Book updatedBook){
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }

    @Transactional
    public void assign(int id, Person selectedPerson){
        Optional<Book> optionalBook = booksRepository.findById(id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            book.setOwner(selectedPerson);
            booksRepository.save(book);
        }
    }

    @Transactional
    public void release(int id){
        Book book = findOne(id);
        book.setOwner(null);
        booksRepository.save(book);
    }

    public List<Book> findByTitleStartingWith(String startingWith){
        return booksRepository.findByTitleStartingWith(startingWith);
    }
}



























