package com.scm.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        String errorMessage = message != null && !message.toString().isBlank()
                ? message.toString()
                : "An unexpected error occurred.";

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            errorMessage = "The page or resource you are looking for does not exist.";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            errorMessage = "You do not have permission to access this resource.";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            errorMessage = "An internal server error occurred. Please try again later.";
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("title", "Error " + statusCode);
        model.addAttribute("message", errorMessage);

        return "error_page";
    }
}