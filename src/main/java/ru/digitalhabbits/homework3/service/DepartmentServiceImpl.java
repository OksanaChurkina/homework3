package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentRequest;
import ru.digitalhabbits.homework3.model.DepartmentResponse;
import ru.digitalhabbits.homework3.model.DepartmentShortResponse;
import ru.digitalhabbits.homework3.model.PersonInfo;
import ru.digitalhabbits.homework3.web.ConflictException;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl
        implements DepartmentService {
    private final DepartmentDao departmentDao;
    private final PersonDao personDao;

    @Nonnull
    @Override
    public List<DepartmentShortResponse> findAllDepartments() {
        // TODO: NotImplemented: получение краткой информации о всех департаментах
        return departmentDao.findAll()
                .stream()
                .map(department -> new DepartmentShortResponse().setName(department.getName()).setId(department.getId()))
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public DepartmentResponse getDepartment(@Nonnull Integer id) {
        // TODO: NotImplemented: получение подробной информации о департаменте и краткой информации о людях в нем.
        //  Если не найдено, отдавать 404:NotFound
        Department department = departmentDao.findById(id);
        try{
            return new DepartmentResponse()
                    .setId(department.getId())
                    .setName(department.getName())
                    .setClosed(department.isClosed())
                    .setPersons(department.getPersons()
                            .stream()
                            .map(person -> new PersonInfo()
                                    .setId(person.getId())
                                    .setFullName(format("%s %s %s", person.getFirstName(), person.getLastName(), person.getMiddleName())))
                            .collect(Collectors.toList()));

        }
        catch (NullPointerException nullPointerException){
            throw new EntityNotFoundException("404:NotFound");
        }
    }

    @Nonnull
    @Override
    public Integer createDepartment(@Nonnull DepartmentRequest request) {
        // TODO: NotImplemented: создание нового департамента
        Department department = new Department().setName(request.getName());
        Department newDepartment = departmentDao.update(department);
        return newDepartment.getId();
    }

    @Nonnull
    @Override
    public DepartmentResponse updateDepartment(@Nonnull Integer id, @Nonnull DepartmentRequest request) {
        // TODO: NotImplemented: обновление данных о департаменте. Если не найдено, отдавать 404:NotFound
        Department department = departmentDao.findById(id);
        try{
            department
                    .setId(department.getId())
                    .setName(department.getName())
                    .setClosed(department.isClosed());

        }
        catch (NullPointerException nullPointerException){
            throw new EntityNotFoundException("404:NotFound");
        }
        Department depToUpdate = departmentDao.update(department);
        DepartmentResponse response = new DepartmentResponse()
                .setId(depToUpdate.getId())
                .setName(depToUpdate.getName())
                .setClosed(depToUpdate.isClosed())
                .setPersons(depToUpdate.getPersons()
                        .stream()
                        .map(person -> new PersonInfo()
                                .setId(person.getId())
                                .setFullName(format("%s %s %s", person.getFirstName(), person.getLastName(), person.getMiddleName())))
                        .collect(Collectors.toList()));
        return response;
    }

    @Override
    public void deleteDepartment(@Nonnull Integer id) {
        // TODO: NotImplemented: удаление всех людей из департамента и удаление самого департамента.
        //  Если не найдено, то ничего не делать
        Department department = departmentDao.findById(id);
        if(department!=null){
            Set<Person> personSet = department.getPersons();
            for(Person person: personSet){
                person.setDepartment(null);
            }
            department.getPersons().clear();
            departmentDao.delete(id);
        }
    }

    @Override
    public void addPersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        // TODO: NotImplemented: добавление нового человека в департамент.
        //  Если не найден человек или департамент, отдавать 404:NotFound.
        //  Если департамент закрыт, то отдавать 409:Conflict
        Department department = departmentDao.findById(departmentId);
        Person person = personDao.findById(personId);
        department.getPersons().add(person);
        person.setDepartment(department);
        try {
            if (department.isClosed()) {
                throw new ConflictException("409:Conflict");
            }
        } catch (NullPointerException n) {
            throw new EntityNotFoundException("404:NotFound");
        }
        departmentDao.update(department);
    }


    @Override
    public void removePersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        // TODO: NotImplemented: удаление человека из департамента.
        //  Если департамент не найден, отдавать 404:NotFound, если не найден человек в департаменте, то ничего не делать
       Department department = departmentDao.findById(departmentId);
       Person person = personDao.findById(personId);
       Set<Person> people = department.getPersons();
       try{
           if(people.contains(person)){
               person.setDepartment(null);
               people.remove(person);
           }
       }
       catch (NullPointerException nullPointerException){
           throw new EntityNotFoundException("404:NotFound");
       }
       departmentDao.update(department);
    }

    @Override
    public void closeDepartment(@Nonnull Integer id) {
        // TODO: NotImplemented: удаление всех людей из департамента и установка отметки на департаменте,
        //  что он закрыт для добавления новых людей. Если не найдено, отдавать 404:NotFound
        Department department = departmentDao.findById(id);
        try {
            Set<Person> people = department.getPersons();

            for (Person p : people) {
                p.setDepartment(null);
            }
            department.getPersons().clear();
            department.setClosed(true);
        }
        catch (NullPointerException n){
            throw new EntityNotFoundException("404:NotFound");
        }
        departmentDao.update(department);

    }
}
