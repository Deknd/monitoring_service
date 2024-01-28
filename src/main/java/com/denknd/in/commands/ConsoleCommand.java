package com.denknd.in.commands;

import com.denknd.entity.Role;
import com.denknd.entity.User;
import com.denknd.port.IGivingAudit;

import java.util.List;

public interface ConsoleCommand<T>  extends IGivingAudit {
    String getCommand();

    T run(String command, User userActive);
    String getHelpCommand(List<Role> roles);
}
