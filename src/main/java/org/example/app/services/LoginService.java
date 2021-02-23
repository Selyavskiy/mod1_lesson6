package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private Logger logger = Logger.getLogger(LoginService.class);
    private final ProjectLoginRepository<LoginForm> loginRepo;


    @Autowired
    public LoginService(ProjectLoginRepository<LoginForm> loginRepo) {
        this.loginRepo = loginRepo;
    }

    public boolean authenticate(LoginForm loginFrom) {

        if (loginFrom.getUsername().equals("root") && loginFrom.getPassword().equals("123")) {
            logger.info("try auth with user-form: " + loginFrom);
            return true;
        }

        for (LoginForm loginForm : loginRepo.retreiveAll()){
            if (loginForm.getUsername().equals(loginFrom.getUsername()) && loginForm.getPassword().equals(loginFrom.getPassword())){
                    logger.info("try auth with user-form: " + loginFrom);
                    return true;
                }
                else{
                    return false;
            }

        }
        return false;
    }

    public boolean isLoginFormOk(LoginForm checkForm){

        if (checkForm.getUsername().equals("root")
                || checkForm.getUsername().isEmpty()
                || checkForm.getPassword().isEmpty())
        {
            return false;
        }

        if(loginRepo.retreiveAll().isEmpty()){
            logger.info("try add first user: " + checkForm.getUsername());
            loginRepo.register(checkForm);
            return true;
        }

        for (LoginForm listForm : loginRepo.retreiveAll()){
            if (checkForm.getUsername().equals(listForm.getUsername())){
                return false;
            }
        }

        logger.info("try add new user: " + checkForm.getUsername());
        loginRepo.register(checkForm);
        return true;

    }
}
