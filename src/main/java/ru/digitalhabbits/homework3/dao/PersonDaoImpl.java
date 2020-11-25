package ru.digitalhabbits.homework3.dao;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Repository;
import ru.digitalhabbits.homework3.domain.Person;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class PersonDaoImpl
        implements PersonDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Person findById(@Nonnull Integer id) {

        return entityManager.find(Person.class, id);
    }

    @Override
    public List<Person> findAll() {

        Query findAllPersons = entityManager.createQuery("Select * from Person", Person.class);
        return findAllPersons.getResultList();
    }

    @Override
    public Person update(Person entity) {

        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Person delete(Integer integer) {

        Person person = this.findById(integer);
        entityManager.remove(person);
        return person;
    }
}
