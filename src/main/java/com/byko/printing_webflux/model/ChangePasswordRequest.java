package com.byko.printing_webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ChangePasswordRequest {

    @NotEmpty @NotNull @NotBlank
    public String password;

    @NotEmpty @NotNull @NotBlank
    public String newPassword;
}
