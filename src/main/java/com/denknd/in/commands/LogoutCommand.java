package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;

import java.util.List;

public class LogoutCommand implements ConsoleCommand<User>{

    private final String COMMAND = "logout";


    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Выход из системы";
    }

    @Override
    public User run(String command, User userActive) {
        if(userActive == null){
            return null;
        }
        if(command.equals(this.COMMAND)){
            return new User();
        }
        System.out.println("Команда не поддерживается: " + command);
        return null;
    }

    @Override
    public String getHelpCommand(List<Role> roles) {
        if(roles != null && !roles.isEmpty()){
            return this.COMMAND+" - выход из системы";
        } else return null;
    }
}
