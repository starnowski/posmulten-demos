package com.github.starnowski.posmulten.demos;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = Application.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public abstract class TestNGSpringContextWithoutGenericTransactionalSupportTests extends AbstractTransactionalTestNGSpringContextTests {
}
