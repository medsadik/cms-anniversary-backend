package com.example.cms_anniversary_backend.repositories;


import com.example.cms_anniversary_backend.entities.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByMatricule(String matricule);

    List<Employee> findByActiveTrue();
    List<Employee> findByActiveTrue(Sort sort);

    List<Employee> findByActiveFalse();

    @Query(
            value = """
        SELECT * FROM employees e
        WHERE e.active = true
          AND EXTRACT(MONTH FROM e.birth_date) = EXTRACT(MONTH FROM CAST(:date AS DATE))
          AND EXTRACT(DAY FROM e.birth_date) = EXTRACT(DAY FROM CAST(:date AS DATE))
        """,
            nativeQuery = true
    )
    List<Employee> findEmployeesWithBirthdayOn(@Param("date") LocalDate date);

    @Query(
            value = """
        SELECT * FROM employees e
        WHERE e.active = true
          AND EXTRACT(MONTH FROM e.entry_date) = EXTRACT(MONTH FROM CAST(:date AS DATE))
          AND EXTRACT(DAY FROM e.entry_date) = EXTRACT(DAY FROM CAST(:date AS DATE))
          AND e.entry_date < :date
        """,
            nativeQuery = true
    )
    List<Employee> findEmployeesWithWorkAnniversaryOn(@Param("date") LocalDate date);




    @Query("SELECT COUNT(e) FROM Employee e WHERE e.active = true")
    long countActiveEmployees();

    @Query(
            value = """
      SELECT COUNT(*) 
      FROM employees e
      WHERE e.active = true
        AND EXTRACT(WEEK FROM e.birth_date) = EXTRACT(WEEK FROM CAST(:today AS DATE))
  """,
            nativeQuery = true
    )
    long countBirthdaysOfTheWeek(@Param("today") LocalDate today);

    @Query(
            value = """
      SELECT COUNT(*) 
      FROM employees e
      WHERE e.active = true
        AND EXTRACT(WEEK FROM e.entry_date) = EXTRACT(WEEK FROM CAST(:today AS DATE))
  """,
            nativeQuery = true
    )
    long countWorkAnniversaryOfTheWeek(@Param("today") LocalDate today);

    @Query(value = """
    SELECT *
    FROM employees e
    WHERE e.active = true
      AND TO_CHAR(CAST(e.birth_date AS date), 'MM-DD')
          BETWEEN TO_CHAR(CAST(:startOfWeek AS date), 'MM-DD')
              AND TO_CHAR(CAST(:endOfWeek AS date), 'MM-DD')
""", nativeQuery = true)
    List<Employee> findEmployeesWithBirthdayThisWeek(
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek);    @Query(value = """
    SELECT *
    FROM employees e
    WHERE e.active = true
      AND TO_CHAR(CAST(e.entry_date AS date), 'MM-DD')
          BETWEEN TO_CHAR(CAST(:startOfWeek AS date), 'MM-DD')
              AND TO_CHAR(CAST(:endOfWeek AS date), 'MM-DD')
""", nativeQuery = true)
    List<Employee> findEmployeesWithWorkAnniversaryThisWeek(
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek);

}
