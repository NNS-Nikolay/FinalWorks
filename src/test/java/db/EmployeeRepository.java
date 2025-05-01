package db;

import entities.Employee.CreateEmployeeRequest;
import entities.Employee.EmployeeDB;
import java.io.IOException;
import java.sql.SQLException;

public interface EmployeeRepository {

    int createEmployee(CreateEmployeeRequest createEmployeeRequest) throws SQLException, IOException;

    void deleteEmployee (int employeeId) throws SQLException;

    EmployeeDB getEmployeeInfo (int employeeId) throws SQLException;
}
