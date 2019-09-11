package com.models;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Client {
    private Long id;

    @NotNull
    @Length(max = 255)
    private String fName;

    @NotNull
    @Length(max = 255)
    private String lName;

    public Client(){
    }

    public Client(Long id, String fName, String lName) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        Client client = (Client) obj;

        return Objects.equals(client.fName,this.fName) &&
                Objects.equals(client.lName,this.lName) &&
                Objects.equals(client.id,this.id);
    }
}
