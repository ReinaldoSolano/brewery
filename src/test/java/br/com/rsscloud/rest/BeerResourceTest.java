package br.com.rsscloud.rest;

import br.com.rsscloud.model.Beer;
import br.com.rsscloud.rest.dto.BeerRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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
        BeerRequest request = new BeerRequest();
        request.name = "IPA";
        request.type = "Ale";

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/beers")
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode());

        given()
                .when().get("/beers")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].name", equalTo("IPA"))
                .body("[0].description", equalTo("Lipi's IPA beer")); // Assert auto-generated description
    }


    @Test
    void testGetSingleFoundAndNotFound() {
        BeerRequest request = new BeerRequest();
        request.name = "Lager";
        request.type = "Lager";

        // Cria a cerveja
        given()
                .contentType(ContentType.JSON)
                .body(request)
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
        BeerRequest request = new BeerRequest();
        request.name = "Witbier";
        request.type = "Wheat";

        // Cria cerveja
        given()
                .contentType(ContentType.JSON)
                .body(request)
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
        BeerRequest request = new BeerRequest();
        request.name = "Pilsen";
        request.type = "Lager";
        given()
                .contentType(ContentType.JSON)
                .body(request)
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

    @Test
    void testGetByType() {
        BeerRequest request = new BeerRequest();
        request.name = "Amber Ale";
        request.type = "Amber";

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/beers")
                .then().statusCode(Response.Status.CREATED.getStatusCode());

        given()
                .when().get("/beers/type/Amber")
                .then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].name", equalTo("Amber Ale"))
                .body("[0].type", equalTo("Amber"));
    }

    @Test
    void testCreateWithDuplicateName() {
        BeerRequest request = new BeerRequest();
        request.name = "Duplicate Beer";
        request.type = "Lager";

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/beers")
                .then().statusCode(Response.Status.CREATED.getStatusCode());

        // Try to create again with the same name
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/beers")
                .then().statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }
}
