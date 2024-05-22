package ru.sverdlov.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sverdlov.library.models.Book;
import ru.sverdlov.library.models.Person;

import java.util.List;

@Repository
public interface  BooksRepository extends JpaRepository<Book, Integer> {
    List<Book> findByOwner(Person owner);

    List<Book> findByTitleStartingWith(String startingWith);
}
