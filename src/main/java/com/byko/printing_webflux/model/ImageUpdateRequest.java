package com.byko.printing_webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ImageUpdateRequest {
    public String description;
    public String title;
    public boolean changeData;
    public String alt;
    public String id;
}
