package server.api;

import commons.DomainModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.Config;
import server.database.DomainModelRepository;

import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DomainValidationControllerTest {
    @Mock
    DomainModelRepository domainModelRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @InjectMocks
    DomainValidationController domainValidationController ;


    @Test
    void testValidate() {
        var dom = new DomainModel();
        when(domainModelRepository.findFirst()).thenReturn(dom);
        when(domainModelRepository.save(any())).thenReturn(dom);
        domainValidationController.setDomainModelUuid(dom.getDomainUuid());
        String result = domainValidationController.validate();
        Assertions.assertEquals(dom.getDomainUuid().toString(), result);
    }
}
