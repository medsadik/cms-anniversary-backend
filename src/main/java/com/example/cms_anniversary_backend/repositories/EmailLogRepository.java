package com.example.cms_anniversary_backend.repositories;



import com.example.cms_anniversary_backend.entities.EmailLog;
import com.example.cms_anniversary_backend.entities.EmailStatus;
import com.example.cms_anniversary_backend.entities.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {

    @Query("SELECT e FROM EmailLog e WHERE " +
            "(:startDate IS NULL OR e.sentAt >= :startDate) AND " +
            "(:endDate IS NULL OR e.sentAt <= :endDate) AND " +
            "(:type IS NULL OR e.type = :type) AND " +
            "(:status IS NULL OR e.status = :status) " +
            "ORDER BY e.sentAt DESC")
    List<EmailLog> findByFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") EmailType type,
            @Param("status") EmailStatus status
    );

    List<EmailLog> findByRecipientEmailOrderBySentAtDesc(String recipientEmail);

    List<EmailLog> findByTypeOrderBySentAtDesc(EmailType type);

    List<EmailLog> findByStatusOrderBySentAtDesc(EmailStatus status);

    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.status = :status")
    long countByStatus(@Param("status") EmailStatus status);

    @Query("SELECT COUNT(e) FROM EmailLog e WHERE e.type = :type AND e.sentAt >= :startDate")
    long countByTypeAndSentAtAfter(@Param("type") EmailType type, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT e FROM EmailLog e WHERE e.sentAt >= :startDate AND e.sentAt <= :endDate ORDER BY e.sentAt DESC")
    List<EmailLog> findBySentAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}