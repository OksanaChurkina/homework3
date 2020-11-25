package ru.digitalhabbits.homework3.dao;

import org.springframework.stereotype.Repository;
import ru.digitalhabbits.homework3.domain.Department;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DepartmentDaoImpl
        implements DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Department findById(@Nonnull Integer integer) {

        return entityManager.find(Department.class, integer);
    }

    @Override
    public List<Department> findAll() {

        Query findAllDepartment = entityManager.createQuery("Select * from Department", Department.class);
        return findAllDepartment.getResultList();
    }

    @Override
    public Department update(Department entity) {

      entityManager.persist(entity);
      return entity;
    }

    @Override
    public Department delete(Integer integer) {

       Department department = this.findById(integer);
       entityManager.remove(department);
       return department;
    }
}
