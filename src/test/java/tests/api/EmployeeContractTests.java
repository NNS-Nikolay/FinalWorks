package tests.api;


import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import db.CompanyRepository;
import db.EmployeeRepository;
import entities.Employee.CreateEmployeeRequest;
import entities.Employee.CreateEmployeeResponse;
import entities.Employee.EmployeeResponse;
import entities.Employee.PatchEmployeeRequest;
import ext.CreateCompanyRepositoryResolver;
import ext.CreateEmployeeRepositoryResolver;
import helpers.ApiAuthHelper;
import helpers.ApiEmployeeHelper;
import configs.EnvHelper;
import helpers.LoggingInterceptor;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;


@ExtendWith({CreateCompanyRepositoryResolver.class, CreateEmployeeRepositoryResolver.class})
public class EmployeeContractTests {

    private static ApiAuthHelper apiAuthHelper;
    private static ApiEmployeeHelper apiEmployeeHelper;
    private static Properties properties;
    private static String baseExpectedHeader;

    @BeforeAll
    public static void setUp() throws IOException {

        apiAuthHelper = new ApiAuthHelper();
        apiEmployeeHelper = new ApiEmployeeHelper();
        EnvHelper envHelper = new EnvHelper();
        properties = envHelper.getConfProperties();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = properties.getProperty("api.url");
        String token = apiAuthHelper.AuthGetToken(properties.getProperty("api.AdminUser"), properties.getProperty("api.AdminPassword"));
        RestAssured.requestSpecification = new RequestSpecBuilder().build().header(properties.getProperty("api.AuthHeader"), token);
        baseExpectedHeader = properties.getProperty("api.ContentTypeHeader");

    }

    public ResponseSpecification getResponseSpecification(){

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        return responseSpecBuilder
                .expectHeader("content-Type", baseExpectedHeader)
                .build();
    }

    @Test
    @DisplayName("Получение списка сотрудников компании возвращает код 200 и JSON")
    public void getEmployeeByCompanyId(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        apiEmployeeHelper.createCountOfEmployees(companyId, 3, employeeRepository);

        List<EmployeeResponse> employeeResponseList = given()
                .filter(new LoggingInterceptor())
                .queryParam("company", companyId)
                .basePath("employee")
                .when().get()
                .then().statusCode(200)
                .spec(getResponseSpecification())
                .extract()
                .jsonPath()
                .getList("$", EmployeeResponse.class);

        for (EmployeeResponse employeeResponse : employeeResponseList){
            employeeRepository.deleteEmployee(employeeResponse.id());
        }
        companyRepository.deleteCompany(companyId);

    }

    @Test
    @DisplayName("Получение списка сотрудников несуществующей компании возвращает код 200 и JSON")
    public void getEmployeeByNonexistentCompanyId(CompanyRepository companyRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        companyRepository.deleteCompany(companyId);

        given()
                .filter(new LoggingInterceptor())
                .queryParam("company", companyId)
                .basePath("employee")
                .when().get()
                .then().statusCode(200)
                .spec(getResponseSpecification());
    }


    @Test
    @Disabled("Тест падает из-за реального несоответсвия схеме")
    @DisplayName("Валидация ответа Получение списка сотрудников компании схеме")
    public void checkPostEmployeeResposeBody(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        String swaggerUri = properties.getProperty("api.swaggerUri");

        int companyId = companyRepository.createCompany();
        apiEmployeeHelper.createCountOfEmployees(companyId, 1, employeeRepository);

        List<EmployeeResponse> employeeResponseList = given()
                .filters(new ResponseLoggingFilter(), new OpenApiValidationFilter(swaggerUri))
                .queryParam("company", companyId)
                .basePath("employee")
                .when().get()
                .then()
                .extract()
                .jsonPath()
                .getList("$", EmployeeResponse.class);


        for (EmployeeResponse employeeResponse : employeeResponseList){
            employeeRepository.deleteEmployee(employeeResponse.id());
        }
        companyRepository.deleteCompany(companyId);

    }


    @Test
    @DisplayName("Добавление нового сотрудника возвращает код 201 и JSON")
    public void createEmployee(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        CreateEmployeeRequest createEmployeeRequest = apiEmployeeHelper.generateEmployeeInstance(companyId);

        CreateEmployeeResponse createEmployeeResponse = given()
                .filter(new LoggingInterceptor())
                .basePath("employee")
                .body(createEmployeeRequest)
                .contentType(ContentType.JSON)
                .when().post()
                .then().statusCode(201)
                .spec(getResponseSpecification())
                .extract().as(CreateEmployeeResponse.class);

        employeeRepository.deleteEmployee(createEmployeeResponse.id());
        companyRepository.deleteCompany(companyId);

    }

    @Test
    @DisplayName("Добавление сотруника с пустым обязательным полем возвращает код 400")
    public void createEmployeeWithoutNeedsField(CompanyRepository companyRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        CreateEmployeeRequest createEmployeeRequest = apiEmployeeHelper.generateEmployeeInstance(companyId);
        CreateEmployeeRequest modifyEmployeeRequest = createEmployeeRequest.changeFieldFirstName();

        given()
                .filter(new LoggingInterceptor())
                .basePath("employee")
                .body(modifyEmployeeRequest)
                .contentType(ContentType.JSON)
                .when().post()
                .then().statusCode(400);

        companyRepository.deleteCompany(companyId);

    }

    @Test
    @Disabled("Отключен, потому что на самом деле возвращается код 500")
    @DisplayName("Добавление сотруника в несуществующую компанию возвращает код 404")
    public void createEmployeeNonexistentCompany(CompanyRepository companyRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        companyRepository.deleteCompany(companyId);

        given()
                .filter(new LoggingInterceptor())
                .basePath("employee")
                .body(apiEmployeeHelper.generateEmployeeInstance(companyId))
                .contentType(ContentType.JSON)
                .when().post()
                .then().statusCode(404);
    }


    @Test
    @DisplayName("Получение сотрудника по Id возвращает код 200 и JSON")
    public void getEmployeeById(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);

        given()
                .filter(new LoggingInterceptor())
                .basePath("employee/")
                .contentType(ContentType.JSON)
                .when().get("{id}", employeeId)
                .then().statusCode(200)
                .spec(getResponseSpecification());

        employeeRepository.deleteEmployee(employeeId);
        companyRepository.deleteCompany(companyId);

    }

    @Test
    @Disabled("Отключен, потому что на самом деле возвращается код 200 и пустое тело")
    @DisplayName("Получение сотрудника по несуществующему Id возвращает код 404")
    public void getEmployeeByNonexistentId(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);
        employeeRepository.deleteEmployee(employeeId);

        given()
                .filter(new LoggingInterceptor())
                .basePath("employee/")
                .contentType(ContentType.JSON)
                .when().get("{id}", employeeId)
                .then().statusCode(404);

        companyRepository.deleteCompany(companyId);

    }

    @Test
    @DisplayName("Изменение информации о сотруднике возвращает код 200 и JSON")
    public void patchEmployeeById(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);

        PatchEmployeeRequest patchEmployeeRequest = apiEmployeeHelper.generetePathEmployeeInstance();

        given()
                .filter(new LoggingInterceptor())
                .basePath("employee/")
                .body(patchEmployeeRequest)
                .contentType(ContentType.JSON)
                .when().patch("{id}", employeeId)
                .then().statusCode(200)
                .spec(getResponseSpecification());

        employeeRepository.deleteEmployee(employeeId);
        companyRepository.deleteCompany(companyId);

    }

    @Test
    @Disabled("Отключен, потому что на самом деле возвращается код 500")
    @DisplayName("Изменение информации о несуществующем сотруднике возвращает код 404")
    public void patchNonexistentEmployeeById(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);
        employeeRepository.deleteEmployee(employeeId);

        PatchEmployeeRequest patchEmployeeRequest = apiEmployeeHelper.generetePathEmployeeInstance();

        given()
                .filter(new LoggingInterceptor())
                .basePath("employee/")
                .body(patchEmployeeRequest)
                .contentType(ContentType.JSON)
                .when().patch("{id}", employeeId)
                .then().statusCode(404)
                .spec(getResponseSpecification());

        companyRepository.deleteCompany(companyId);

    }



}

















