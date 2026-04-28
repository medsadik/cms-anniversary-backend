package com.example.cms_anniversary_backend.dtos;

import com.example.cms_anniversary_backend.entities.EmailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailTestDto {
    private EmailType templateType;
    private String email;
    private List<String> ccList;
}
