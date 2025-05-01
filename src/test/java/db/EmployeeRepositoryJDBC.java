package db;

import entities.Employee.CreateEmployeeRequest;
import entities.Employee.EmployeeDB;
import helpers.LoggingInterceptor;
import io.qameta.allure.Step;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;


public class EmployeeRepositoryJDBC implements EmployeeRepository{

    private final Connection connection;
    private final LoggingInterceptor loggingInterceptor;

    public EmployeeRepositoryJDBC(Connection connection)  {
        this.connection = connection;
        loggingInterceptor = new LoggingInterceptor();
    }

    @Override
    @Step("Создание сотрудника SQL")
    public int createEmployee(CreateEmployeeRequest createEmployeeRequest) throws SQLException, IOException {

        String CREATE_EMP = "insert into employee (company_id, first_name, last_name, middle_name, email, phone, birthdate, avatar_url) values (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_EMP, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, createEmployeeRequest.companyId());
            preparedStatement.setString(2, createEmployeeRequest.firstName());
            preparedStatement.setString(3, createEmployeeRequest.lastName());
            preparedStatement.setString(4, createEmployeeRequest.middleName());
            preparedStatement.setString(5, createEmployeeRequest.email());
            preparedStatement.setString(6, createEmployeeRequest.phone());
            preparedStatement.setDate(7, Date.valueOf(LocalDate.now()));
            preparedStatement.setString(8, createEmployeeRequest.url());
            preparedStatement.executeUpdate();

            loggingInterceptor.attachData("" + preparedStatement);

            try (ResultSet result = preparedStatement.getGeneratedKeys()) {
                if (result.next()) {
                    return result.getInt("id");
                } else {
                    throw new SQLException("Не удалось создать сотрудника, Id не задан");
                }
            }
        }
    }

    @Override
    @Step("Удаление сотрудника SQL")
    public void deleteEmployee(int employeeId) throws SQLException  {
        String DEL_EMPLOYEE = "delete from employee where id = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(DEL_EMPLOYEE)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.execute();
            loggingInterceptor.attachData("" + preparedStatement);
        }

    }

    @Override
    @Step("Получение информации о сотруднике SQL")
    public EmployeeDB getEmployeeInfo(int employeeId) throws SQLException  {
        String GET_EMPLOYEE = "select * from employee where id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_EMPLOYEE)) {

            preparedStatement.setInt(1, employeeId);
            loggingInterceptor.attachData("" + preparedStatement);

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    return new EmployeeDB(result.getInt("id"),
                            result.getBoolean("is_active"),
                            result.getString("create_timestamp"),
                            result.getString("change_timestamp"),
                            result.getString("first_name"),
                            result.getString("last_name"),
                            result.getString("middle_name"),
                            result.getString("phone"),
                            result.getString("email"),
                            result.getString("birthdate"),
                            result.getString("avatar_url"),
                            result.getInt("company_id")
                    );
                } else {
                    throw new SQLException("Сотрудник не найден по Id:" + employeeId);
                }
            }
        }
    }
}















