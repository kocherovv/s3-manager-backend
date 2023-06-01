package net.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.example.domain.enums.Role;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserCreateDto {

    @Size(max = 20, min = 3)
    private String name;

    @Email
    private String email;

    @Size(max = 16, min = 8)
    private String password;

    @Builder.Default
    private Role role = Role.USER;
}
