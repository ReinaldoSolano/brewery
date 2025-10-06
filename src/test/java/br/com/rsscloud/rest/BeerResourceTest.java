package br.com.rsscloud.rest;

import br.com.rsscloud.model.Beer;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BeerResourceTest {

    @BeforeEach
    @Transactional
    void setup() {
        // Limpa a tabela antes de cada teste
        Beer.deleteAll();
    }

    @Test
    void testListAllEmpty() {
        given()
                .when().get("/beers")
                .then()
                .statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    void testCreateAndListAll() {
        Beer beer = new Beer();
        beer.name = "IPA";
        beer.description = "Strong";
        beer.type = "Ale";

        ValidatableResponse response = given()
                .contentType(ContentType.JSON)
                .body(beer)
                .when().post("/beers")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        given()
                .when().get("/beers")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].name", equalTo("IPA"));
    }

    @Test
    void testCreateWithIdShouldReturnBadRequest() {
        Beer beer = new Beer();
        beer.id = 100L; // id não pode vir preenchido
        beer.name = "Stout";
        beer.description = "Dark";
        beer.type = "Ale";

        given()
                .contentType(ContentType.JSON)
                .body(beer)
                .when().post("/beers")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    void testGetSingleFoundAndNotFound() {
        Beer beer = new Beer();
        beer.name = "Lager";
        beer.description = "Smooth";
        beer.type = "Lager";

        // Cria a cerveja
        given()
                .contentType(ContentType.JSON)
                .body(beer)
                .when().post("/beers")
                .then().statusCode(Response.Status.CREATED.getStatusCode());

        // Busca lista para pegar o id
        Long id = given()
                .when().get("/beers")
                .then().statusCode(200)
                .extract().jsonPath().getLong("[0].id");

        // Busca existente
        given()
                .when().get("/beers/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Lager"));

        // Busca inexistente
        given()
                .when().get("/beers/{id}", 99999L)
                .then()
                .statusCode(204)
                .body(equalTo(""));
    }

    @Test
    void testUpdateFoundAndNotFound() {
        Beer beer = new Beer();
        beer.name = "Witbier";
        beer.description = "Citrus";
        beer.type = "Wheat";

        // Cria cerveja
        given()
                .contentType(ContentType.JSON)
                .body(beer)
                .when().post("/beers")
                .then().statusCode(Response.Status.CREATED.getStatusCode());

        // Busca o id
        Long id = given()
                .when().get("/beers")
                .then().extract().jsonPath().getLong("[0].id");

        // Atualiza
        Beer updated = new Beer();
        updated.name = "Witbier Updated";
        updated.description = "Citrus Updated";
        updated.type = "Wheat Updated";

        given()
                .contentType(ContentType.JSON)
                .body(updated)
                .when().put("/beers/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Witbier Updated"))
                .body("description", equalTo("Citrus Updated"))
                .body("type", equalTo("Wheat Updated"));

        // Atualiza inexistente
        given()
                .contentType(ContentType.JSON)
                .body(updated)
                .when().put("/beers/{id}", 55555L)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testDeleteFoundAndNotFound() {
        Beer beer = new Beer();
        beer.name = "Pilsen";
        beer.description = "Classic";
        beer.type = "Lager";
        given()
                .contentType(ContentType.JSON)
                .body(beer)
                .when().post("/beers")
                .then().statusCode(Response.Status.CREATED.getStatusCode());

        Long id = given()
                .when().get("/beers")
                .then().extract().jsonPath().getLong("[0].id");

        // Delete existente
        given()
                .when().delete("/beers/{id}", id)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

        // Delete inexistente
        given()
                .when().delete("/beers/{id}", 999999L)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}