package com.tarun.passwordvalidator.controller;

import com.tarun.passwordvalidator.dto.PasswordReportDto;
import com.tarun.passwordvalidator.dto.PasswordRequestDto;
import com.tarun.passwordvalidator.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for password validation web interface.
 */
@Controller
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("passwordRequestDto", new PasswordRequestDto());
        return "index";
    }

    @PostMapping("/validate")
    public String validatePassword(@Valid PasswordRequestDto passwordRequestDto,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }

        String username = passwordRequestDto.getUsername();
        String password = passwordRequestDto.getPassword();

        // Call service to get report
        PasswordReportDto reportDto = passwordService.validate(password, username);

        model.addAttribute("report", reportDto);
        model.addAttribute("username", username);
        model.addAttribute("password", password); // optional, for showing in feedback

        return "index"; // Stay on the same page
    }
}