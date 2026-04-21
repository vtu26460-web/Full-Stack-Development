package com.example.task9;

import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepository {

    public Employee getEmployee(){
        return new Employee(101,"Gayathri","IT");
    }

}