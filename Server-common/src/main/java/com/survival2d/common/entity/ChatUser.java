package com.survival2d.common.entity;

import com.tvd12.ezyfox.annotation.EzyId;
import com.tvd12.ezyfox.database.annotation.EzyCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EzyCollection
public class ChatUser extends ChatEntity {

    @EzyId
    private Long id;
    private String username;
    private String password;
    private String firstName = "";
    private String lastName = "";
    private String avatarUrl = "";
    private boolean online;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
