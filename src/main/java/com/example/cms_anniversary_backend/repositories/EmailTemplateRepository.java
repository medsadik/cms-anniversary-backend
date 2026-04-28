package com.example.cms_anniversary_backend.repositories;



import com.example.cms_anniversary_backend.entities.EmailTemplate;
import com.example.cms_anniversary_backend.entities.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    Optional<EmailTemplate> findByTypeAndActiveTrue(EmailType type);

    List<EmailTemplate> findByActiveTrue();

    Optional<EmailTemplate> findByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByName(String name);

    List<EmailTemplate> findByType(EmailType type);
}
