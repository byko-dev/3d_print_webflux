package com.byko.printing_webflux.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "photos")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class PhotoData {

    @Id
    private String id;
    private String title;
    private String alt;
    private String description;
    private String date;
    private String fileId;
}
