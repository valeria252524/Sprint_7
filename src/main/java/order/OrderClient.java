package order;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String API_V1_ORDERS = "/api/v1/orders";

    public Response create(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(API_V1_ORDERS);
    }
}
