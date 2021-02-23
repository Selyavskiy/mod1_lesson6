package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.example.web.dto.LoginForm;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LoginRepository implements ProjectLoginRepository<LoginForm> {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final List<LoginForm> repo = new ArrayList<>();


    @Override
    public List<LoginForm> retreiveAll() {
        return repo;
    }

    @Override
    public void register(LoginForm loginForm) {
        repo.add(loginForm);
        logger.info("add new user: " + loginForm.getUsername());
    }

    @Override
    public boolean removeItemByUsername(String loginUsernameToRemove) {
        for (LoginForm loginForm : retreiveAll()) {
            if (loginForm.getUsername().equals(loginUsernameToRemove)) {
                logger.info("remove book completed: " + loginForm);
                return repo.remove(loginForm);
            }
        }
        return false;
    }
}
