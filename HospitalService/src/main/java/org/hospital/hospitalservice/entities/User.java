package org.hospital.hospitalservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity(name = "users")
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "chat_id", unique = true)
    private long chatID;
    @Column(name = "name")
    private String name;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "token", unique = true)
    private String token;

    public User(String name, String secondName, String token) {
        this.name = name;
        this.secondName = secondName;
        this.token = token;
        this.chatID = -1;
    }

    public User updateChatID(long chatID) {
        this.chatID = chatID;
        return this;
    }

    @Override
    public String toString() {
        return "\nname : [%s]\n second name : [%s]\nchatID : [%s]\ntoken : [%s]"
                .formatted(
                        this.name,
                        this.secondName,
                        this.chatID,
                        this.token
                );
    }
}
