package site.autzone.event.springboot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = EventHandlerApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("event")
@TestPropertySource("classpath:")
public class EmployeeRestControllerIntegrationTest {

  @Before
  public void setUp() throws Exception {
    System.out.println("set up.");
  }

  @Test
  public void test() {
    System.out.println("test");
  }
}
