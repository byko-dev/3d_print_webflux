package com.byko.printing_webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class LastTimeActivityResponse {
    private long lastTimeActive;
    private String nameAndLastName;
    private String photoId;
}
