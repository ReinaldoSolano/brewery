package br.com.rsscloud.service;

import br.com.rsscloud.model.Beer;
import br.com.rsscloud.repository.BeerRepository;
import br.com.rsscloud.rest.dto.BeerRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class BeerServiceTest {

    @Inject
    BeerService beerService;

    @InjectMock
    BeerRepository beerRepository;

    private BeerRequest beerRequest;

    @BeforeEach
    void setUp() {
        beerRequest = new BeerRequest();
        beerRequest.name = "Test Beer";
        beerRequest.type = "IPA";
    }

    @Test
    void testCreate_Success() {
        when(beerRepository.findByName(anyString())).thenReturn(Collections.emptyList());
        when(beerRepository.findBySku(anyString())).thenReturn(Collections.emptyList());

        beerService.create(beerRequest);

        ArgumentCaptor<Beer> beerCaptor = ArgumentCaptor.forClass(Beer.class);
        // This is a bit tricky because persist is a method on the entity itself.
        // We can't directly verify beer.persist().
        // However, we can verify the interactions with the repository that happen before.
        verify(beerRepository, times(1)).findByName("Test Beer");
        verify(beerRepository, atLeastOnce()).findBySku(anyString());
    }

    @Test
    void testCreate_DuplicateName() {
        when(beerRepository.findByName("Test Beer")).thenReturn(List.of(new Beer()));

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            beerService.create(beerRequest);
        });

        assertEquals("name must be unique to create beer", exception.getMessage());
        assertEquals(400, exception.getResponse().getStatus());
        verify(beerRepository, times(1)).findByName("Test Beer");
        verify(beerRepository, never()).findBySku(anyString());
    }

    @Test
    void testValidName_Unique() {
        when(beerRepository.findByName("Unique Name")).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> beerService.validName("Unique Name"));
        verify(beerRepository, times(1)).findByName("Unique Name");
    }

    @Test
    void testValidName_Duplicate() {
        when(beerRepository.findByName("Duplicate Name")).thenReturn(List.of(new Beer()));
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            beerService.validName("Duplicate Name");
        });
        assertEquals("name must be unique to create beer", exception.getMessage());
        assertEquals(400, exception.getResponse().getStatus());
        verify(beerRepository, times(1)).findByName("Duplicate Name");
    }

    @Test
    void testGenerateSKU_CollisionOnce() {
        // Arrange
        String firstSKU = "LIPI-" + UUID.randomUUID();
        String secondSKU = "LIPI-" + UUID.randomUUID();

        // Mock repository to simulate one collision
        when(beerRepository.findByName(anyString())).thenReturn(Collections.emptyList());
        when(beerRepository.findBySku(anyString()))
                .thenReturn(List.of(new Beer())) // First call finds a collision
                .thenReturn(Collections.emptyList()); // Second call finds no collision

        // Act
        beerService.create(beerRequest);

        // Assert
        verify(beerRepository, times(2)).findBySku(anyString());
    }
}