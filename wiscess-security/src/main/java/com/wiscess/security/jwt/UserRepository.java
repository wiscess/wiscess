package com.wiscess.security.jwt;

public interface UserRepository {

    public String findByToken(String token);
    public void insert(String token, String user);
    public void remove(String token);
}
