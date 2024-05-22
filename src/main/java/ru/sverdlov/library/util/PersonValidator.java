package ru.sverdlov.library.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.sverdlov.library.models.Person;
import ru.sverdlov.library.services.PeopleService;

@Component
public class PersonValidator implements Validator {
    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService){ this.peopleService = peopleService; }

    @Override
    public boolean supports(Class<?> aClass){ return Person.class.equals(aClass); }

    @Override
    public void validate (Object o, Errors errors){
        Person person = (Person) o;

        if(!peopleService.findByFullName(person.getFullName()).isEmpty())
            errors.rejectValue("fullName", "", "Person with this full name already exists");
    }
}
