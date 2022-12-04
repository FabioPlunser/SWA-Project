package at.ac.uibk.swa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SwaApplicationTests {

    @Test
    public void DummyTest() {
        Assertions.assertEquals(2 + 2, 4);
    }

}
