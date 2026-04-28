package com.example.cms_anniversary_backend.mappers;


import com.example.cms_anniversary_backend.dtos.EmailTemplateCreateDTO;
import com.example.cms_anniversary_backend.dtos.EmailTemplateDTO;
import com.example.cms_anniversary_backend.entities.EmailTemplate;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailTemplateMapper {

    EmailTemplateDTO toDTO(EmailTemplate template);

    List<EmailTemplateDTO> toDTOList(List<EmailTemplate> templates);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    EmailTemplate toEntity(EmailTemplateCreateDTO dto);


    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(EmailTemplateCreateDTO dto, @MappingTarget EmailTemplate template);;
}