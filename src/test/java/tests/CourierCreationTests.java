package tests;
import courier.Courier;
import courier.CourierClient;
import courier.CourierLoginResponse;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static courier.CourierCreds.credsFromCourier;
import static courier.CourierGenerator.randomCourier;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.assertEquals;

public class CourierCreationTests {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private CourierClient courierClient;
    private int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        courierClient = new CourierClient();
    }

    @Test
    @Step("Курьера можно создать и авторизоваться")
    public void courierCanBeCreated() {
        Courier courier = randomCourier();
        Response response = courierClient.create(courier);

        assertEquals("Ожидается код 201 при создании", SC_CREATED, response.statusCode());
        assertEquals("Ожидается ok: true", true, response.jsonPath().getBoolean("ok"));

        // логин, чтобы получить id
        Response loginResponse = courierClient.login(credsFromCourier(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }
    @Test
    @Step("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        Courier courier = randomCourier();
        courierClient.create(courier);

        // Пытаемся создать того же курьера второй раз
        Response duplicateResponse = courierClient.create(courier);

        assertEquals("Ожидается код 409 при создании дубликата", 409, duplicateResponse.statusCode());
        assertEquals("Ожидается сообщение об ошибке", "Этот логин уже используется. Попробуйте другой.", duplicateResponse.jsonPath().getString("message"));

        Response loginResponse = courierClient.login(credsFromCourier(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }

    @Test
    @Step("Ошибка при создании курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier()
                .setPassword("1234")
                .setFirstName("TestName");

        Response response = courierClient.create(courier);

        assertEquals("Ожидается код 400 при отсутствии логина", 400, response.statusCode());
        assertEquals("Ожидается сообщение об ошибке", "Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }
    @Test
    @Step("Ошибка при создании курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier()
                .setLogin("test_login")
                .setFirstName("TestName");

        Response response = courierClient.create(courier);

        assertEquals("Ожидается код 400 при отсутствии пароля", 400, response.statusCode());
        assertEquals("Ожидается сообщение об ошибке", "Недостаточно данных для создания учетной записи", response.jsonPath().getString("message"));
    }
    @Test
    @Step("Ошибка при создании курьера с логином, который уже есть")
    public void cannotCreateCourierWithSameLogin() {
        Courier courier1 = new Courier()
                .setLogin("Login123")
                .setPassword("Password123")
                .setFirstName("Courier1");

        courierClient.create(courier1);

        Courier courier2 = new Courier()
                .setLogin("Login123")
                .setPassword("pass123")
                .setFirstName("Courier2");

        Response response = courierClient.create(courier2);

        assertEquals("Ожидается код 409 при создании курьера с тем же логином", 409, response.statusCode());
        assertEquals("Ожидается сообщение об ошибке", "Этот логин уже используется. Попробуйте другой.", response.jsonPath().getString("message"));


        Response loginResponse = courierClient.login(credsFromCourier(courier1));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }


    @After
    public void tearTest() {
        courierClient.delete(id);
    }
}