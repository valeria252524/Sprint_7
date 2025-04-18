package courier;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String API_V1_COURIER = "api/v1/courier";
    private static final String API_V1_COURIER_LOGIN = "api/v1/courier/login";

    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(API_V1_COURIER);
    }

    public Response login(CourierCreds courierCreds) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreds)
                .when()
                .post(API_V1_COURIER_LOGIN);
    }

    public Response delete(int id) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(API_V1_COURIER + "/" + id);
    }
}