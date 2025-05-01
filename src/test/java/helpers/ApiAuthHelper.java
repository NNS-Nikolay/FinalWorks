package helpers;

import entities.Authorization.AuthRequest;
import entities.Authorization.AuthResponse;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;

public class ApiAuthHelper {

    @Step("Получение токена для авторизаци API")
    public String AuthGetToken(String username, String password){

        AuthRequest authRequest = new AuthRequest(username, password);
        AuthResponse authResponse = given()
                .basePath("auth/login")
                .body(authRequest)
                .contentType(ContentType.JSON)
                .when().post()
                .as(AuthResponse.class);

        return authResponse.userToken();
    }

}
