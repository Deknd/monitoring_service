package com.denknd.services;

import com.denknd.entity.User;
import com.denknd.in.commands.ConsoleCommand;

import java.util.Map;

public interface AuditService {
    void addAction(Map<String, ConsoleCommand> consoleCommandMap, String commandAndParam, User activeUser);
}
