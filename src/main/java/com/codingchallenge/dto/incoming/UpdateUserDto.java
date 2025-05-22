package com.codingchallenge.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserDto {
    @NotNull(message = "Name cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "Name must be alphanumeric and between 3 and 20 characters long")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "Password must be alphanumeric and between 8 and 20 characters long")
    private String password;

    @NotNull(message = "Old password cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "Old password must be alphanumeric and between 8 and 20 characters long")
    private String oldPassword;
}
