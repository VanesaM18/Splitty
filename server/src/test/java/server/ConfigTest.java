package server;

import commons.DomainModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.database.DomainModelRepository;

import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ConfigTest {
    @Mock
    DomainModelRepository domainModelRepository;
    @InjectMocks
    Config config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRandom() {
        Random result = config.getRandom();
        Random result2 = config.getRandom();
        Assertions.assertNotSame(result, result2);
    }

    @Test
    void testGetDomainUuid() {
        var dom = new DomainModel();
        when(domainModelRepository.findFirst()).thenReturn(dom);
        when(domainModelRepository.save(any())).thenReturn(dom);

        UUID result = config.getDomainUuid();
        Assertions.assertEquals(dom.getDomainUuid(), result);
    }
}
