package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.in.commands.functions.LongIdParserFromRawParameters;
import com.denknd.in.commands.functions.MyFunction;
import com.denknd.port.IGivingAudit;
import com.denknd.services.UserService;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
@Setter
public class UserCommand implements ConsoleCommand<String>{
    
    private final String COMMAND = "user";
    private final String EMAIL_PARAM = "email=";
    private final String ID_PARAM = "id=";
    private final UserService userService;
    private MyFunction<String[], Long> longIdParserFromRawParameters;

    public UserCommand(UserService userService) {
        this.userService = userService;
        this.longIdParserFromRawParameters = new LongIdParserFromRawParameters();
    }

    @Override
    public String getCommand() {
        return this.COMMAND;
    }

    @Override
    public String getMakesAction() {
        return "Выдает информацию о пользователе";
    }

    @Override
    public String run(String command, User userActive) {
        var userRole = Role.builder().roleName("USER").build();
        var adminRole = Role.builder().roleName("ADMIN").build(); 
        if(userActive == null){
            return null;
        }
        if(userActive.getRoles().contains(userRole)){
            return userToStringConvert(userActive);
        } else if(userActive.getRoles().contains(adminRole)){
            var commandAndParam = command.split(" ");
            var id = this.longIdParserFromRawParameters.apply(commandAndParam, this.ID_PARAM);
            var email = Arrays.stream(commandAndParam)
                    .filter(param -> param.contains(this.EMAIL_PARAM))
                    .map(param -> param.replace(this.EMAIL_PARAM, ""))
                    .findFirst().orElse(null);
            if(id != null && this.userService.existUser(id)){
                var user = this.userService.getUserById(id);
                return this.userToStringConvert(user);
            } if(email != null && this.userService.existUserByEmail(email)){
                var user = this.userService.getUserByEmail(email);
                return this.userToStringConvert(user);
            }
        }
        return null;
    }

    private String userToStringConvert(User userActive) {
        return "id: "+userActive.getUserId() + ", " + userActive.getLastName() + " " + userActive.getFirstName() + ", " + userActive.getEmail();
    }

    @Override
    public String getHelpCommand(List<Role> roles) {
        var userRole = Role.builder().roleName("USER").build();
        var adminRole = Role.builder().roleName("ADMIN").build();
        if(roles == null || roles.isEmpty()){
            return null;
        }
        var param = roles.contains(adminRole) ?
                "\nДополнительные параметры:\n        "+
                this.EMAIL_PARAM + " - показывает пользователя с данным email(пример: "+this.EMAIL_PARAM+"test@mail.ru),\n        "+
                this.ID_PARAM + " - показывает пользователя с данным айди(пример: "+this.ID_PARAM+"2345)": "";
        if(roles.contains(userRole) || roles.contains(adminRole)){
            return this.COMMAND +" - Команда для просмотра данных о пользователе" + param;
        }
        return null;
    }
}
