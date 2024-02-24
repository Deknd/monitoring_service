//package com.denknd.repository;
//
//import com.denknd.config.PostgresContainer;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//
//import java.security.SecureRandom;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class TestContainer {
//
//  protected static PostgresContainer postgresContainer;
//  private static SecureRandom random;
//  private static String ALLOWED_CHARACTERS;
//  @BeforeAll
//  static void beforeAll(){
//    postgresContainer = new PostgresContainer();
//    postgresContainer.start();
//    var liquibaseConfig = mock(LiquibaseConfigImpl.class);
//    when(liquibaseConfig.changelog()).thenReturn("db/changelog/changelog-test.xml");
//    when(liquibaseConfig.defaultSchema()).thenReturn("public");
//    when(liquibaseConfig.technicalSchema()).thenReturn("public");
//
//    var dataBaseConnection = new DataBaseConnectionImpl(postgresContainer.getDbConfig());
//    var liquibaseMigration = new LiquibaseMigration(dataBaseConnection, liquibaseConfig);
//    liquibaseMigration.migration();
//    random = new SecureRandom();
//    ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//
//  }
//  @AfterAll
//  static void afterAll() {
//    postgresContainer.stop();
//    postgresContainer=null;
//  }
//
//
//
//  public String generateRandomLogin(int length) {
//    StringBuilder login = new StringBuilder(length);
//    for (int i = 0; i < length; i++) {
//      int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
//      login.append(ALLOWED_CHARACTERS.charAt(randomIndex));
//    }
//    return login.toString();
//  }
//}
