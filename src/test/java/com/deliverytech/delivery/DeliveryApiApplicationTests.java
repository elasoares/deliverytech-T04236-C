package com.deliverytech.delivery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DeliveryApiApplication.class)
@ActiveProfiles("test")
class DeliveryApiApplicationTests {
    @Test
    void contextLoads(){}
}
