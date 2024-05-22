package ru.sverdlov.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.sverdlov.library.models.Book;
import ru.sverdlov.library.models.Person;

import jakarta.validation.Valid;
import ru.sverdlov.library.services.BookService;
import ru.sverdlov.library.services.PeopleService;

import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController( BookService bookService, PeopleService peopleService){this.bookService = bookService;
       this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model, @RequestParam(name = "page", required=false, defaultValue = "0") int page,
                        @RequestParam(name = "books_per_page", required = false, defaultValue = "30") int booksPerPage,
                        @RequestParam(name = "sort_by_year", required = false, defaultValue = "false") boolean sortByYear){

        model.addAttribute("books", bookService.findAll(page, booksPerPage, sortByYear));
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person){
        model.addAttribute("book", bookService.findOne(id));

        Optional<Person> bookOwner = bookService.findOwnerById(id);

        if(bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", peopleService.findAll());

        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book){
        // эквивалентно model.addAttribute("book", Book book);
        return "books/new";
    }

    @PostMapping("")
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return "books/new";

        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model){
        model.addAttribute("book", bookService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") @Valid Book book, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return "/books/edit";

        bookService.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        bookService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/assign")
    public String assign(@ModelAttribute("person") Person selectedPerson, @PathVariable("id") int id){
        // У selectedPerson назначено только поле id, остальные поля - null
        bookService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id){
        bookService.release(id);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String search(){
        return "books/search";
    }

    @PostMapping("/search")
    public String search(Model model, @RequestParam(name="query") String query){
        model.addAttribute("books", bookService.findByTitleStartingWith(query));
        return "books/search";
    }
}


























