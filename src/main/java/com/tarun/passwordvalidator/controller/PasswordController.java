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
        var report = passwordService.validate(password, username);

        // Convert to DTO for view
        PasswordReportDto reportDto = new PasswordReportDto();
        reportDto.setScore(report.getScore());
        reportDto.setStrengthLevel(report.getStrengthLevel().toString());
        reportDto.setSuggestions(report.getSuggestions());

        model.addAttribute("report", reportDto);
        model.addAttribute("username", username);
        model.addAttribute("password", password); // optional, for showing in feedback

        return "result";
    }
}