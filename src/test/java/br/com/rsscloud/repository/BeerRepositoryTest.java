package br.com.rsscloud.repository;

import br.com.rsscloud.model.Beer;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
public class BeerRepositoryTest {

    @InjectSpy
    BeerRepository beerRepository;

    private PanacheQuery<Beer> panacheQuery;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        // Create a mock PanacheQuery before each test
        panacheQuery = Mockito.mock(PanacheQuery.class);
    }

    @Test
    void testFindByType() {
        // Arrange
        Beer beer = new Beer();
        beer.type = "IPA";
        List<Beer> beerList = Collections.singletonList(beer);

        // Mock the chain of calls: find().list()
        Mockito.when(panacheQuery.list()).thenReturn(beerList);
        doReturn(panacheQuery).when(beerRepository).find("type", "IPA");

        // Act
        List<Beer> result = beerRepository.findByType("IPA");

        // Assert
        assertEquals(1, result.size());
        assertEquals("IPA", result.get(0).type);
        Mockito.verify(beerRepository).find("type", "IPA");
    }

    @Test
    void testFindBySku() {
        // Arrange
        Beer beer = new Beer();
        beer.sku = "12345";
        List<Beer> beerList = Collections.singletonList(beer);

        // Mock the chain of calls: find().list()
        Mockito.when(panacheQuery.list()).thenReturn(beerList);
        doReturn(panacheQuery).when(beerRepository).find("sku", "12345");

        // Act
        List<Beer> result = beerRepository.findBySku("12345");

        // Assert
        assertEquals(1, result.size());
        assertEquals("12345", result.get(0).sku);
        Mockito.verify(beerRepository).find("sku", "12345");
    }

    @Test
    void testFindByName() {
        // Arrange
        Beer beer = new Beer();
        beer.name = "Test Beer";
        List<Beer> beerList = Collections.singletonList(beer);

        // Mock the chain of calls: find().list()
        Mockito.when(panacheQuery.list()).thenReturn(beerList);
        doReturn(panacheQuery).when(beerRepository).find("name", "Test Beer");

        // Act
        List<Beer> result = beerRepository.findByName("Test Beer");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Beer", result.get(0).name);
        Mockito.verify(beerRepository).find("name", "Test Beer");
    }
}
