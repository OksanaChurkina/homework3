package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl
        implements PersonService {

    private final PersonDao personDao;

    DepartmentInfo departmentInfo = new DepartmentInfo();

    @Nonnull
    @Override
    public List<PersonResponse> findAllPersons() {
        // TODO: NotImplemented: получение информации о всех людях во всех отделах
        List<PersonResponse> list = personDao.findAll()
                .stream()
                 .map(p -> new PersonResponse()
                         .setId(p.getId())
                         .setAge(p.getAge())
                         .setFullName(format("%s %s %s", p.getFirstName(), p.getLastName(), p.getMiddleName()))
                         .setDepartment(getDepartmentInfo(p.getDepartment())))
                .collect(Collectors.toList());
        return list;

    }

    @Nonnull
    @Override
    public PersonResponse getPerson(@Nonnull Integer id) {
        // TODO: NotImplemented: получение информации о человеке. Если не найдено, отдавать 404:NotFound
        Person p = personDao.findById(id);
        try {
            return new PersonResponse()
                    .setFullName(format("%s %s %s", p.getFirstName(), p.getLastName(), p.getMiddleName()))
                    .setId(p.getId())
                    .setAge(p.getAge())
                    .setDepartment(getDepartmentInfo(p.getDepartment()));
        }catch (NullPointerException exception){
            throw new EntityNotFoundException("404:NotFound");
        }
    }

    @Nonnull
    @Override
    public Integer createPerson(@Nonnull PersonRequest request) {
        // TODO: NotImplemented: создание новой записи о человеке
        Person person = new Person()
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setLastName(request.getLastName())
                .setAge(request.getAge());
        Person newPerson = personDao.update(person);
        return newPerson.getId();
    }

    @Nonnull
    @Override
    public PersonResponse updatePerson(@Nonnull Integer id, @Nonnull PersonRequest request) {
        // TODO: NotImplemented: обновление информации о человеке. Если не найдено, отдавать 404:NotFound
        Person person = personDao.findById(id);
        try {
            person
                    .setFirstName(request.getFirstName())
                    .setMiddleName(request.getMiddleName())
                    .setLastName(request.getLastName())
                    .setAge(request.getAge());
        } catch (NullPointerException exception) {
            throw new EntityNotFoundException("404:NotFound");
        }
        Person updated = personDao.update(person);

        return new PersonResponse()
                .setId(updated.getId())
                .setFullName(format("%s %s %s", updated.getFirstName(), updated.getLastName(), updated.getMiddleName()))
                .setAge(updated.getAge());
    }

    @Override
    public void deletePerson(@Nonnull Integer id) {
        // TODO: NotImplemented: удаление информации о человеке и удаление его из отдела. Если не найдено, ничего не делать
        Person person = personDao.findById(id);
        if(person!=null){
            personDao.delete(id);
        }
    }

    private DepartmentInfo getDepartmentInfo(Department department)
    {
        Integer id = department.getId();
        String name = department.getName();
        return departmentInfo.setId(id).setName(name);
    }
}
