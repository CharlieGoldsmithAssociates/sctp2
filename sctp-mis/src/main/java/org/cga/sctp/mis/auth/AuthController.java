package org.cga.sctp.mis.auth;

import org.cga.sctp.auth.AuthService;
import org.cga.sctp.mis.config.CustomAccessDeniedHandler;
import org.cga.sctp.mis.core.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationProvider authManager;

    @GetMapping("/login")
    public ModelAndView loginForm(@ModelAttribute LoginForm loginForm, HttpServletRequest request) {
        ModelAndView modelAndView;
        String errorMessage = (String) request.getAttribute(CustomAccessDeniedHandler.ERROR_MESSAGE);
        modelAndView = view("auth/login", "loginForm", loginForm);
        if (errorMessage != null) {
            setDangerMessage(modelAndView, errorMessage);
        }
        return modelAndView;
    }

    @PostMapping("/login")
    public String doLogin(
            HttpServletRequest request,
            Model model,
            @Validated @ModelAttribute LoginForm loginForm,
            BindingResult result) {

        Authentication authenticationResult;

        if (result.hasErrors()) {
            model.addAttribute(loginForm);
            return "auth/login";
        }

        String errorMessage = (String) request.getAttribute(CustomAccessDeniedHandler.ERROR_MESSAGE);
        if (errorMessage != null) {
            model.addAttribute(loginForm);
            setDangerMessage(model, errorMessage);
            return "auth/login";
        }

        try {
            UsernamePasswordAuthenticationToken token;
            token = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());
            token.setDetails(new WebAuthenticationDetails(request));
            authenticationResult = authManager.authenticate(token);

            if (authenticationResult == null) {
                logger.warn("Failed to find provider for authentication type {}", token.getClass());
                setDangerMessage(model, "Cannot login at the moment, please contact website administrator.");
                return "auth/login";
            } else {
                SecurityContextHolder.getContext().setAuthentication(authenticationResult);
            }
        } catch (AuthenticationException exception) {
            setDangerMessage(model, exception.getMessage());
            return "auth/login";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes attributes) {
        setSuccessFlashMessage("You have been successfully logged out.", attributes);
        return "redirect:/auth/login";
    }
}

