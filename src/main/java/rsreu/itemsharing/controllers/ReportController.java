package rsreu.itemsharing.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rsreu.itemsharing.infrastructure.Birt;

@Controller
public class ReportController {

    @Autowired
    private Birt birt;

    @GetMapping("/report")
    public String report() {
        return "report";
    }

    @PostMapping("/report")
    public void report(@RequestParam String id, HttpServletResponse response, HttpServletRequest request) {
        birt.generatePDF_aboutUser(id, response, request);
    }
}

