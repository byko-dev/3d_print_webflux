package com.byko.printing_webflux.database;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProjectData {

    @Id
    private String id;
    private String nameAndLastName;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String projectFileId;
    private String date;
    private int orderStatus;
    private String ipAddress;
}
