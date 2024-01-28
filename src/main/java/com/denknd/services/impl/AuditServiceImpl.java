package com.denknd.services.impl;

import com.denknd.entity.Audit;
import com.denknd.entity.User;
import com.denknd.in.commands.ConsoleCommand;
import com.denknd.port.AuditRepository;
import com.denknd.port.IGivingAudit;
import com.denknd.services.AuditService;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;
    @Override
    public void addAction(Map<String, ConsoleCommand> consoleCommandMap, String commandAndParam, User activeUser) {
        var iGivingAudit = consoleCommandMap.keySet().stream()
                .filter(commandAndParam::contains)
                .map(consoleCommandMap::get)
                .filter(consoleCommand -> consoleCommand instanceof IGivingAudit)
                .map(consoleCommand -> (IGivingAudit) consoleCommand)
                .findFirst()
                .orElse(new IGivingAudit() {
                    @Override
                    public String getCommand() {
                        return "не известная команда: " + commandAndParam;
                    }

                    @Override
                    public String getMakesAction() {
                        return "Не известно";
                    }

                });
       if (activeUser != null){
           var operation = "Введенная команда: " + commandAndParam + ", выполнилась команда: " + iGivingAudit.getCommand() + " - " + iGivingAudit.getMakesAction();

           var audit = Audit.builder()
                   .operationTime(OffsetDateTime.now())
                   .user(activeUser)
                   .operation(operation)
                   .build();

           this.auditRepository.save(audit);
       }


    }
}
