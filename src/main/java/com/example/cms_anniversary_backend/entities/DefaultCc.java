package com.example.cms_anniversary_backend.entities;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "default_cc_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultCc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Employee (mandatory)
    @OneToOne(optional = false)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;
}
