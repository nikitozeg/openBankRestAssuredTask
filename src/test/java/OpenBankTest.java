import Entities.Request.UserRequest;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import Entities.Response.*;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.*;

public class OpenBankTest {

    String BASE_URI = "https://reqres.in";
    String USER_URI = "/api/users";

    @Test
    public void checkResponseWithAssertTest() {
        User userResponse = given()
                .baseUri(BASE_URI)
                .log().everything()
                .contentType(ContentType.JSON)
                .when()
                .get(USER_URI + "?page=2")
                .then()
                .extract()
                .body().as(User.class);

        assertNotNull(userResponse.getPage());
        assertNotNull(userResponse.getPer_page());
        assertNotNull(userResponse.getTotal());
        assertNotNull(userResponse.getTotal_pages());


        for (Data data : userResponse.getData()) {
            assertNotNull(data.getId());
            assertNotNull(data.getEmail());
            assertNotNull(data.getFirst_name());
            assertNotNull(data.getLast_name());
            assertNotNull(data.getAvatar());
        }

        Ad ad = userResponse.getAd();
        assertNotNull(ad.getCompany());
        assertNotNull(ad.getText());
        assertNotNull(ad.getUrl());
    }

    @Test
    public void checkResponseWithJsonSchemaTest() {
        given()
                .baseUri(BASE_URI)
                .log().everything()
                .contentType(ContentType.JSON)
                .when()
                .get(USER_URI + "?page=2")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("JsonSchemaFile.json"));

    }

    @Test
    public void checkCreatedUserTest() {
        given()
                .baseUri(BASE_URI)
                .log().everything()
                .contentType(ContentType.JSON)
                .when()
                .post(USER_URI)
                .then()
                .statusCode(201)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("CreateUserResponseSchema.json"));


        UserRequest ur = new UserRequest()
                .setJob("QA automation engineer")
                .setName("Nikita");


        CreateUserResponse cr = given()
                .baseUri(BASE_URI)
                .log().everything()
                .contentType(ContentType.JSON)
                .body(ur)
                .when()
                .post(USER_URI)
                .then()
                .statusCode(201)
                .extract()
                .body().as(CreateUserResponse.class);

        assertEquals(cr.getJob(), ur.getJob(), "Job is not the same as created");
        assertEquals(cr.getName(), ur.getName(), "Name is not the same as created");
    }

}
