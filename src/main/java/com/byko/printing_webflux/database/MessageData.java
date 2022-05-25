package com.byko.printing_webflux.database;

import com.byko.printing_webflux.enums.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class MessageData {

    @Id
    private String id;
    private String messageContent;
    private String fileId;
    private String date;
    private User user;
    private String ipAddress;
    private String projectId;
    private String adminId;
}
