package com.denknd.in;

import com.denknd.services.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.mock;

class ConsoleTest {


    private Console console;
    private AuditService auditService;
    private Scanner scanner;

    @BeforeEach
    void setUp() {
        this.auditService = mock(AuditService.class);
        this.scanner = mock(Scanner.class);
        this.console = new Console(scanner, auditService);
    }

    @Test
    void run() {
    }

    @Test
    void addCommand() {
    }

    @Test
    void testAddCommand() {
    }

    @Test
    void commands() {
    }

    @Test
    void getActiveUser() {
    }
}