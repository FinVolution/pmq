package com.ppdai.infrastructure.rest.mq;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RestApplication.class})
@ActiveProfiles("TEST")
abstract public class AbstractIntegrationTest {

}
