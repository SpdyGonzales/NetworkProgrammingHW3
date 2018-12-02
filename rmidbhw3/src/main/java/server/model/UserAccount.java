package server.model;

import common.UserAccountDTO;

import java.io.Serializable;

public class UserAccount implements Serializable, UserAccountDTO {
    private String username;
    private String password;

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
