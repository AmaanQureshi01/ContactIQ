package com.scm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContactForm {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Address")
    private String email;
    @NotBlank(message = "Invalid Phone Number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone number")
    private String phone;
    private String address;
    private String description;
    private String socialLinks1;
    private String linkdin;
    private boolean favorite;
    private MultipartFile contactImage;

}
