package com.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class User {
    @NotNull
    @Length(max = 255)
    private String fName;

    @NotNull
    @Length(max = 255)
    private String lName;

    @NotNull
    @Length(max = 255)
    private String username;

    @NotNull
    @Length(max = 255)
    private String password;

    public User(){
    }

    public User(String fName, String lName, String username, String password) {
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.password = password;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        User user = (User) obj;

        return Objects.equals(user.fName,this.fName) &&
                Objects.equals(user.lName,this.lName) &&
                Objects.equals(user.username,this.username) &&
                Objects.equals(user.password,this.password);
    }
}
