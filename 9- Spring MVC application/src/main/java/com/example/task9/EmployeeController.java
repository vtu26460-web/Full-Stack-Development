package com.example.task9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeController {

    @Autowired
    EmployeeRepository repo;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/employee")
    public String getEmployee(Model model){

        Employee emp = repo.getEmployee();
        model.addAttribute("emp",emp);

        return "employee";
    }
}