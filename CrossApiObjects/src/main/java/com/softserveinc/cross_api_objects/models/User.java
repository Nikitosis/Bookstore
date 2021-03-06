package com.softserveinc.cross_api_objects.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.softserveinc.cross_api_objects.security.AuthProvider;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class User {
    public enum Gender{
        @JsonProperty("MALE")
        MALE,

        @JsonProperty("FEMALE")
        FEMALE
    };

    private Long id;

    @Length(max = 255)
    private String fName;

    @Length(max = 255)
    private String lName;

    @NotNull
    @NotEmpty
    @Length(max = 255,min = 6)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Length(max=255)
    private String country;

    @Length(max=255)
    private String city;

    private Gender gender;

    @Length(max=13)
    private String phone;

    @Length(max=255)
    private String email;

    @Length(max=255)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String avatarLink;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal money;

    private Boolean isEmailVerified;

    private Boolean isSubscribedToNews;

    @JsonIgnore
    private String verificationToken;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AuthProvider authProvider;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Role> roles;


    public User(){
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Boolean getSubscribedToNews() {
        return isSubscribedToNews;
    }

    public void setSubscribedToNews(Boolean subscribedToNews) {
        isSubscribedToNews = subscribedToNews;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        User user = (User) obj;

        return Objects.equals(user.id,this.id) &&
                Objects.equals(user.fName,this.fName) &&
                Objects.equals(user.lName,this.lName) &&
                Objects.equals(user.username,this.username) &&
                Objects.equals(user.city,this.city) &&
                Objects.equals(user.country,this.country) &&
                Objects.equals(user.phone,this.phone)&&
                Objects.equals(user.email,this.email) &&
                Objects.equals(user.money,this.money) &&
                Objects.equals(user.avatarLink,this.avatarLink) &&
                Objects.equals(user.password,this.password)&&
                Objects.equals(user.isEmailVerified,this.isEmailVerified) &&
                Objects.equals(user.isSubscribedToNews,this.isSubscribedToNews) &&
                Objects.equals(user.verificationToken,this.verificationToken) &&
                Objects.deepEquals(user.roles,this.roles)&&
                Objects.equals(user.gender,this.gender);
    }
}
