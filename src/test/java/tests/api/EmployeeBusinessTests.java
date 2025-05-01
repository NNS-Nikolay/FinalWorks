package tests.api;

import db.CompanyRepository;
import db.EmployeeRepository;
import entities.Employee.CreateEmployeeRequest;
import entities.Employee.EmployeeDB;
import entities.Employee.EmployeeResponse;
import entities.Employee.PatchEmployeeRequest;
import ext.CreateCompanyRepositoryResolver;
import ext.CreateEmployeeRepositoryResolver;
import helpers.ApiEmployeeHelper;
import helpers.ApiAuthHelper;
import configs.EnvHelper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({CreateCompanyRepositoryResolver.class, CreateEmployeeRepositoryResolver.class})
public class EmployeeBusinessTests {

    private static ApiAuthHelper apiAuthHelper;
    private static ApiEmployeeHelper apiEmployeeHelper;
    private Integer companyId;


    @BeforeAll
    public static void setUp() throws IOException {

        EnvHelper envHelper = new EnvHelper();
        apiAuthHelper = new ApiAuthHelper();
        apiEmployeeHelper = new ApiEmployeeHelper();
        Properties properties = envHelper.getConfProperties();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = properties.getProperty("api.url");
        String token = apiAuthHelper.AuthGetToken(properties.getProperty("api.AdminUser"), properties.getProperty("api.AdminPassword"));
        RestAssured.requestSpecification = new RequestSpecBuilder().build().header(properties.getProperty("api.AuthHeader"), token);

    }

    @AfterEach
    public void tearDown(CompanyRepository companyRepository) throws SQLException {
        if (companyId != null){
            companyRepository.deleteCompany(companyId);
        }
    }


    @Test
    @DisplayName("Получение списка сотрудников компании")
    public void getListEmployeeByCompanyId(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);

        EmployeeDB employeeDB = employeeRepository.getEmployeeInfo(employeeId);

        List<EmployeeResponse> employeeResponseList = apiEmployeeHelper.getListOfEmployess(companyId);

        for (EmployeeResponse employeeResponse : employeeResponseList){

            assertEquals(employeeDB.id(),          employeeResponse.id());
            assertEquals(employeeDB.first_name(),  employeeResponse.firstName());
            assertEquals(employeeDB.last_name(),   employeeResponse.lastName());
            assertEquals(employeeDB.middle_name(), employeeResponse.middleName());
            assertEquals(employeeDB.company_id(),  employeeResponse.companyId());
            assertEquals(employeeDB.email(),       employeeResponse.email());
            assertEquals(employeeDB.avatar_url(),  employeeResponse.avatar_url());
            assertEquals(employeeDB.phone(),       employeeResponse.phone());
            assertEquals(employeeDB.birthdate(),   employeeResponse.birthdate());
            assertEquals(employeeDB.is_active(),   employeeResponse.isActive());

            employeeRepository.deleteEmployee(employeeResponse.id());
        }
    }

    @Test
    @DisplayName("Получение списка из трех сотрудников компании")
    public void getListOfThreeEmployersByCompanyId(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        int count = 3;
        companyId = companyRepository.createCompany();
        apiEmployeeHelper.createCountOfEmployees(companyId, count, employeeRepository);

        List<EmployeeResponse> employeeResponseList = apiEmployeeHelper.getListOfEmployess(companyId);

        assertEquals(count, employeeResponseList.size());

        for (EmployeeResponse employeeResponse : employeeResponseList){
            employeeRepository.deleteEmployee(employeeResponse.id());
        }
    }

    @Test
    @Disabled("Отключен. Ошибка api: сотрудник создается с полем email = null")
    @DisplayName("Добавление нового сотрудника")
    public void CreateEmployer(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        companyId = companyRepository.createCompany();
        CreateEmployeeRequest createEmployeeRequest = apiEmployeeHelper.generateEmployeeInstance(companyId);

        int employeeId = apiEmployeeHelper.ApiCreateEmployee(createEmployeeRequest);
        EmployeeDB employeeDB = employeeRepository.getEmployeeInfo(employeeId);

        assertEquals(employeeDB.company_id(),  createEmployeeRequest.companyId());
        assertEquals(employeeDB.first_name(),  createEmployeeRequest.firstName());
        assertEquals(employeeDB.last_name(),   createEmployeeRequest.lastName());
        assertEquals(employeeDB.middle_name(), createEmployeeRequest.middleName());
        assertEquals(employeeDB.company_id(),  createEmployeeRequest.companyId());
        assertEquals(employeeDB.email(),       createEmployeeRequest.email());
        assertEquals(employeeDB.avatar_url(),  createEmployeeRequest.url());
        assertEquals(employeeDB.phone(),       createEmployeeRequest.phone());
        assertEquals(employeeDB.birthdate(),   createEmployeeRequest.birthdate());
        assertTrue(employeeDB.is_active());

        employeeRepository.deleteEmployee(employeeId);
    }

    @Test
    @DisplayName("Получение сотрудника по id")
    public void getEmployee(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);

        EmployeeResponse employeeResponse = apiEmployeeHelper.getEmployeeById(employeeId);
        EmployeeDB employeeDB = employeeRepository.getEmployeeInfo(employeeId);

        assertEquals(employeeDB.id(),          employeeResponse.id());
        assertEquals(employeeDB.first_name(),  employeeResponse.firstName());
        assertEquals(employeeDB.last_name(),   employeeResponse.lastName());
        assertEquals(employeeDB.middle_name(), employeeResponse.middleName());
        assertEquals(employeeDB.company_id(),  employeeResponse.companyId());
        assertEquals(employeeDB.email(),       employeeResponse.email());
        assertEquals(employeeDB.avatar_url(),  employeeResponse.avatar_url());
        assertEquals(employeeDB.phone(),       employeeResponse.phone());
        assertEquals(employeeDB.birthdate(),   employeeResponse.birthdate());
        assertEquals(employeeDB.is_active(),   employeeResponse.isActive());

        employeeRepository.deleteEmployee(employeeId);
    }


    @Test
    @Disabled("Отключен. Ошибка api: поле phone не обновляется")
    @DisplayName("Изменение информации о сотруднике")
    public void patchEmployeeById(CompanyRepository companyRepository, EmployeeRepository employeeRepository) throws SQLException, IOException {

        companyId = companyRepository.createCompany();
        int employeeId = apiEmployeeHelper.createEmployee(companyId, employeeRepository);

        PatchEmployeeRequest patchEmployeeRequest = apiEmployeeHelper.generetePathEmployeeInstance();
        apiEmployeeHelper.pathEmployeeById(employeeId, patchEmployeeRequest);

        EmployeeDB employeeDB = employeeRepository.getEmployeeInfo(employeeId);

        assertEquals(patchEmployeeRequest.lastName(), employeeDB.last_name());
        assertEquals(patchEmployeeRequest.email(),    employeeDB.email());
        assertEquals(patchEmployeeRequest.url(),      employeeDB.avatar_url());
        assertEquals(patchEmployeeRequest.phone(),  employeeDB.phone());
        assertEquals(patchEmployeeRequest.isActive(), employeeDB.is_active());

        employeeRepository.deleteEmployee(employeeId);
    }

}













