package helpers;

import db.EmployeeRepository;
import entities.Employee.CreateEmployeeRequest;
import entities.Employee.CreateEmployeeResponse;
import entities.Employee.EmployeeResponse;
import entities.Employee.PatchEmployeeRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import org.instancio.Instancio;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.instancio.Select.field;

public class ApiEmployeeHelper {

    private ApiAuthHelper authHelper;

    public ApiEmployeeHelper() {authHelper = new ApiAuthHelper();
    }

    public CreateEmployeeRequest generateEmployeeInstance(int companyId) {

        return Instancio.of(CreateEmployeeRequest.class)
                .generate(field("email"), generators -> generators.net().email())
                .set(field("companyId"),companyId)
                .set(field("birthdate"), LocalDate.now().toString()).withSeed(1).create();

    }

    public PatchEmployeeRequest generetePathEmployeeInstance(){

        return  Instancio.of(PatchEmployeeRequest.class)
                .generate(field("email"), generators -> generators.net().email())
                .withSeed(2).create();

    }

    @Step("Создание списка из ({count}) сотрудников ")
    public void createCountOfEmployees(int companyId, int count, EmployeeRepository employeeRepository) throws SQLException, IOException {

        for (int i = 1; i <= count; i++) {
            CreateEmployeeRequest createEmployeeRequest = generateEmployeeInstance(companyId);
            employeeRepository.createEmployee(createEmployeeRequest);
        }

    }

    @Step("Создание сотрудника")
    public int createEmployee(int companyId, EmployeeRepository employeeRepository) throws SQLException, IOException {

        CreateEmployeeRequest createEmployeeRequest = generateEmployeeInstance(companyId);
        return employeeRepository.createEmployee(createEmployeeRequest);

    }

    @Step("Получение списка сотрудников по компании id: {companyId}")
    public List<EmployeeResponse> getListOfEmployess(int companyId){

        return given()
                .queryParam("company", companyId)
                .basePath("employee")
                .when().get()
                .then()
                .extract()
                .jsonPath()
                .getList("$", EmployeeResponse.class);
    }

    @Step("Создание сотрудника API")
    public int ApiCreateEmployee(CreateEmployeeRequest createEmployeeRequest ){

        return given()
                .basePath("employee")
                .body(createEmployeeRequest)
                .contentType(ContentType.JSON)
                .when().post()
                .then()
                .extract().as(CreateEmployeeResponse.class).id();

    }

    @Step("Получение сотрудника по id: {employeeId}")
    public EmployeeResponse getEmployeeById(int employeeId){

        return given()
                .basePath("employee/")
                .contentType(ContentType.JSON)
                .when().get("{id}", employeeId)
                .then()
                .extract().as(EmployeeResponse.class);
    }

    @Step("Изменение данных сотрудника по id: {employeeId}")
    public void pathEmployeeById(int employeeId, PatchEmployeeRequest patchEmployeeRequest){

        given()
                .basePath("employee/")
                .body(patchEmployeeRequest)
                .contentType(ContentType.JSON)
                .when().patch("{id}", employeeId);
    }

}











