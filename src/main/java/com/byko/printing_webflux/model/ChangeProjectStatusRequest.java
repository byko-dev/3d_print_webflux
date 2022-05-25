package com.byko.printing_webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ChangeProjectStatusRequest {

    @NotEmpty @NotNull @NotBlank
    public String projectId;

    @Min(0) @Max(3)
    public int newStatus;
}
