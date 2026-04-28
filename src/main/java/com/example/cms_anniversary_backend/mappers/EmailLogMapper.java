package com.example.cms_anniversary_backend.mappers;


import com.example.cms_anniversary_backend.dtos.EmailLogDTO;
import com.example.cms_anniversary_backend.entities.EmailLog;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailLogMapper {

    EmailLogDTO toDTO(EmailLog log);

    List<EmailLogDTO> toDTOList(List<EmailLog> logs);

    EmailLog toEntity(EmailLogDTO dto);
}
