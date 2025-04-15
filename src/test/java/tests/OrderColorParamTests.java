package tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import order.Order;
import order.OrderClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrderColorParamTests {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private final List<String> color;
    private OrderClient orderClient;

    public OrderColorParamTests(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()} // без цвета
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderClient = new OrderClient();
    }

    @Test
    @Step("Тестируется создание заказа с цветом/цветами: {0}")
    public void testCreateOrderWithVariousColors() {
        Order order = new Order(color);
        Response response = orderClient.create(order);

        assertEquals("Ожидается код 201 при создании заказа", SC_CREATED, response.statusCode());
        assertNotNull("Ожидается, что в ответе будет track", response.jsonPath().get("track"));
    }
}
