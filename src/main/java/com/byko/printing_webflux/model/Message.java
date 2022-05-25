package com.byko.printing_webflux.model;

import com.byko.printing_webflux.enums.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Message {
    private String messageContent;
    private String fileId;
    public String fileName;
    public String date;
    public User user;
}
