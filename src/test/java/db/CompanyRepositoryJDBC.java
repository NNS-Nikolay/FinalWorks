package db;

import configs.EnvHelper;
import helpers.LoggingInterceptor;
import io.qameta.allure.Step;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class CompanyRepositoryJDBC implements CompanyRepository {

    private final Connection connection;
    private final LoggingInterceptor loggingInterceptor;

    public CompanyRepositoryJDBC(Connection connection) {
        this.connection = connection;
        loggingInterceptor = new LoggingInterceptor();
    }

    @Override
    @Step("Создание компании SQL")
    public int createCompany() throws SQLException, IOException {

        EnvHelper envHelper = new EnvHelper();
        Properties properties = envHelper.getConfProperties();

        String TestCompanyName = properties.getProperty("db.TestCompanyName");
        String TestCompanyDescription = properties.getProperty("db.TestCompanyDescription");

        String CREATE_COMPANY = "insert into company (name, description) values (?,?)";


        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_COMPANY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, TestCompanyName);
            preparedStatement.setString(2, TestCompanyDescription);
            preparedStatement.executeUpdate();

            loggingInterceptor.attachData("" + preparedStatement);

            try (ResultSet result = preparedStatement.getGeneratedKeys()) {
                if (result.next()) {
                    return result.getInt("id");
                } else {
                    throw new SQLException("Не удалось создать компанию, Id не задан");
                }
            }
        }
    }

    @Override
    @Step("Удаление компании SQL")
    public void deleteCompany(int companyId) throws SQLException {

        String DEL_COMPANY = "delete from company where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(DEL_COMPANY)) {
            preparedStatement.setInt(1, companyId);
            preparedStatement.execute();
            loggingInterceptor.attachData("" + preparedStatement);
        }
    }


}

















