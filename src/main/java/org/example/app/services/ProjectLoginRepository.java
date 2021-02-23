package org.example.app.services;

import java.util.List;

public interface ProjectLoginRepository<T> {

    List<T> retreiveAll();

    void register(T loginForm);

    boolean removeItemByUsername(String loginUsernameToRemove);
}
