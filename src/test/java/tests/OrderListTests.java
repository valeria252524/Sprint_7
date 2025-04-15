package tests;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import order.OrderList;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class OrderListTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private OrderList orderList;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        orderList = new OrderList();
    }

    @Test
    @Step("Получение списка заказов")
    public void canGetOrderListSuccessfully() {
        Response getListResponse = orderList.get();
        assertEquals("Ожидается код 200", SC_OK, getListResponse.statusCode());
        assertNotNull("Список заказов должен присутствовать", getListResponse.jsonPath().getList("orders"));
        assertTrue("Список заказов не должен быть пустым", getListResponse.jsonPath().getList("orders").size() > 0);
    }
}
