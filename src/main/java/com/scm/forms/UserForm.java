package com.scm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserForm {
    @NotBlank(message = "Username is required")
    @Size(min=3, message = "Min 3 Characters is required")
    private String name;
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email Address")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min=6, message = "Min 6 Characters is required")
    private String password;
    private String about;
    @Size(min = 10,max = 13, message = "Invalid Phone Number")
    private String phoneNumber;

}
