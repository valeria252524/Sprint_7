package tests;
import courier.Courier;
import courier.CourierClient;
import courier.CourierCreds;
import courier.CourierLoginResponse;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static courier.CourierGenerator.randomCourier;
import static org.junit.Assert.*;
import static org.apache.http.HttpStatus.*;

public class CourierLoginTests {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private CourierClient courierClient;
    private Courier courier;
    private int id;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        courierClient = new CourierClient();
        id = 0;
    }

    @Test
    @Step("Курьера можно создать и успешно авторизоваться")
    public void courierCanLoginSuccessfully() {
        courier = randomCourier();
        courierClient.create(courier);

        Response loginResponse = courierClient.login(CourierCreds.credsFromCourier(courier));

        assertEquals("Ожидается код 200 при успешной авторизации", SC_OK, loginResponse.statusCode());

        id = loginResponse.as(CourierLoginResponse.class).getId();
        assertTrue("Ожидается, что вернётся id", id > 0);
    }

    @Test
    @Step("Ошибка при авторизации без логина")
    public void cannotLoginWithoutLogin() {
        Courier courierWithoutLogin = new Courier().setPassword("password123");
        CourierCreds creds = CourierCreds.credsFromCourier(courierWithoutLogin);

        Response response = courierClient.login(creds);

        assertEquals("Ожидается код 400 при отсутствии логина", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Недостаточно данных для входа", response.jsonPath().getString("message"));
    }

    @Test
    @Step("Ошибка при авторизации без пароля")
    public void cannotLoginWithoutPassword() {
        Courier courierWithoutPassword = new Courier().setLogin("login123");
        CourierCreds creds = CourierCreds.credsFromCourier(courierWithoutPassword);

        Response response = courierClient.login(creds);

        assertEquals("Ожидается код 400 при отсутствии пароля", SC_BAD_REQUEST, response.statusCode());
        assertEquals("Недостаточно данных для входа", response.jsonPath().getString("message"));
    }

    @Test
    @Step("Ошибка при авторизации с неправильным логином")
    public void cannotLoginWithWrongLogin() {
        courier = randomCourier();
        courierClient.create(courier);

        CourierCreds wrongCreds = new CourierCreds("wrongLogin", courier.getPassword());
        Response response = courierClient.login(wrongCreds);

        assertEquals("Ожидается код 404 при неправильном логине", SC_NOT_FOUND, response.statusCode());
        assertEquals("Учетная запись не найдена", response.jsonPath().getString("message"));
    }

    @Test
    @Step("Ошибка при авторизации с неправильным паролем")
    public void cannotLoginWithWrongPassword() {
        courier = randomCourier();
        courierClient.create(courier);

        CourierCreds wrongCreds = new CourierCreds(courier.getLogin(), "wrongPassword");
        Response response = courierClient.login(wrongCreds);

        assertEquals("Ожидается код 404 при неправильном пароле", SC_NOT_FOUND, response.statusCode());
        assertEquals("Учетная запись не найдена", response.jsonPath().getString("message"));
    }


    @Test
    @Step("Ошибка при авторизации несуществующенр курьера")
    public void cannotLoginNonExistentCourier() {
        CourierCreds creds = new CourierCreds("nonexistentuser", "somepassword");

        Response response = courierClient.login(creds);

        assertEquals("Ожидается код 404 при логине несуществующего пользователя", SC_NOT_FOUND, response.statusCode());
        assertEquals("Учетная запись не найдена", response.jsonPath().getString("message"));
    }

    @After
    public void tearDown() {
        // Удаляем курьера только если он был успешно создан и залогинен
        if (id > 0) {
            courierClient.delete(id);
            id = 0;
        }
    }
}

