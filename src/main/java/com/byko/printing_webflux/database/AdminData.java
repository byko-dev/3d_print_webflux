package com.byko.printing_webflux.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admins")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AdminData {

    @Id
    private String id;
    private String username;
    private String password; //bcrypt
    private String nameAndLastName;
    private Long lastTimeActivity;
    private String photoId;
}
