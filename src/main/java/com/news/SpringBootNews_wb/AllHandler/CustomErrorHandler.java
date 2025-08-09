package com.news.SpringBootNews_wb.AllHandler;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorHandler implements ErrorController {
    
    
    @RequestMapping("/error")
    public String handler(HttpServletRequest request){

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {

            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404.html";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/500.html";
            }
             else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "errors/403.html";
            }
            
        }

        return "error/generic";
    }
}
