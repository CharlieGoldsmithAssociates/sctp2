package org.cga.sctp.mis.auth;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.servlet.CaptchaServletUtil;
import org.cga.sctp.audit.UserOnboardedEvent;
import org.cga.sctp.auth.AuthService;
import org.cga.sctp.messaging.EmailBody;
import org.cga.sctp.messaging.EmailMessage;
import org.cga.sctp.messaging.EmailRecipient;
import org.cga.sctp.messaging.MessagingService;
import org.cga.sctp.mis.config.CustomAccessDeniedHandler;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.TemplateService;
import org.cga.sctp.mis.system.SystemService;
import org.cga.sctp.mis.utils.CaptchaService;
import org.cga.sctp.user.SystemRole;
import org.cga.sctp.user.User;
import org.cga.sctp.user.UserService;
import org.cga.sctp.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationProvider authManager;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private SystemService systemService;

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

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        Captcha captcha = captchaService.build();
        request.getSession(true).setAttribute(CaptchaService.ATTRIBUTE_NAME, captcha);
        CaptchaServletUtil.writeImage(response, captcha.getImage());
    }

    @GetMapping("/register")
    public ModelAndView getRegistrationForm(@ModelAttribute("form") RegistrationForm form) {
        return view("auth/register");
    }

    private String createResetLinkForUser(User user, String resetToken) {
        return systemService.makeUrl("auth/reset", user.getId(), resetToken);
    }

    @PostMapping("/register")
    public ModelAndView processRegistration(
            HttpServletRequest request,
            @Validated @ModelAttribute("form") RegistrationForm form,
            BindingResult result) {

        User user;

        if (result.hasErrors()) {
            return view("auth/register");
        }

        if (!captchaService.isValidCaptcha(request.getSession(false), form.getCaptcha())) {
            result.addError(new FieldError("form", "captcha", "Invalid captcha."));
            return view("auth/register");
        }

        if (userService.emailExists(form.getEmail())) {
            return setDangerMessage(view("auth/register"), "Selected email address is not available.");
        }

        if (userService.userNameExists(form.getUsername())) {
            return setDangerMessage(view("auth/register"), "Selected username is not available.");
        }

        user = new User();
        user.setActive(true);
        user.setSystemUser(false);
        user.setRole(SystemRole.ROLE_GUEST);
        user.setLastName(form.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        user.setFirstName(form.getFirstName());
        user.setIpAddress(request.getRemoteAddr());
        user.setEmail(form.getEmail().toLowerCase());
        user.setPassword(authService.generatePassword());
        user.setUserName(form.getUsername().toLowerCase());
        user.setStatusText("Recently self-onboarded");

        userService.saveUser(user);

        publishEvent(new UserOnboardedEvent(user));

        EmailRecipient emailRecipient = new EmailRecipient(user.makeFullName(), user.getEmail());
        String resetToken = authService.createPasswordResetToken(user.getPassword());

        Map<String, Object> model = Map.of(
                "recipient", emailRecipient,
                "username", user.getUsername(),
                "passwordResetLink", createResetLinkForUser(user, resetToken)
        );

        EmailMessage message = new EmailMessage(
                emailRecipient,
                new EmailBody(
                        templateService.renderView(view("messaging/email/account-registered").addAllObjects(model)),
                        true
                ),
                "SCTP MIS: Account Registered"
        );

        messagingService.sendEmail(message);

        return setSuccessMessage(view("auth/registration-done"),
                "Account successfully created. Check your email for details.");
    }

    @GetMapping("/reset")
    public ModelAndView getResetForm(@ModelAttribute("form") PasswordResetForm form) {
        return view("auth/reset");
    }

    @GetMapping("/reset/{user-id}/{token}")
    public ModelAndView getPasswordEntryForm(
            @PathVariable("user-id") Long id,
            @PathVariable("token") String token,
            @ModelAttribute("form") PasswordUpdateForm form) {
        User user;
        if ((user = userService.findById(id)) != null && user.isActive()) {
            if (authService.isValidToken(token, user.getPassword())) {
                form.setUserId(id);
                form.setToken(token);
                return view("auth/password-update");
            }
        }
        return setDangerMessage(view("auth/action-done"), "Invalid or expired reset link.");
    }

    @PostMapping("/reset/update")
    public ModelAndView processPasswordUpdate(
            HttpServletRequest request,
            @Validated @ModelAttribute("form") PasswordUpdateForm form,
            BindingResult result) {
        User user;

        if (result.hasErrors()) {
            return view("auth/password-update");
        }

        if (!captchaService.isValidCaptcha(request.getSession(false), form.getCaptcha())) {
            result.addError(new FieldError("form", "captcha", "Invalid captcha."));
            return view("auth/password-update");
        }

        if ((user = userService.findById(form.getUserId())) == null || !user.isActive()) {
            return setDangerMessage(view("auth/action-done"), "Session expired. Please try again");
        }

        if (authService.isValidToken(form.getToken(), user.getPassword())) {
            user.setPassword(authService.hashPassword(form.getPassword()));
            user.setModifiedAt(LocalDateTime.now());
            userService.saveUser(user);

            Map<String, Object> context = Map.of(
                    "recipient", user.makeFullName(),
                    "when", DateUtils.format(LocalDateTime.now()),
                    "ipAddress", request.getRemoteAddr()
            );

            messagingService.sendEmail(new EmailMessage(
                    new EmailRecipient(user.makeFullName(), user.getEmail()),
                    new EmailBody(templateService.renderView("messaging/email/password-updated",context), true),
                    "SCTP MIS: Password Updated"
            ));
            return setSuccessMessage(view("auth/action-done"), "Password changed successfully");
        }
        return setDangerMessage(view("auth/action-done"), "Session expired. Please try again");
    }

    @PostMapping("/reset")
    public ModelAndView processResetForm(
            HttpServletRequest request,
            @ModelAttribute("form") PasswordResetForm form,
            BindingResult result) {

        User user;

        if (result.hasErrors()) {
            return view("auth/reset");
        }

        if (!captchaService.isValidCaptcha(request.getSession(false), form.getCaptcha())) {
            result.addError(new FieldError("form", "captcha", "Invalid captcha."));
            return view("auth/reset");
        }

        if ((user = userService.findByUserName(form.getUsername())) != null && user.isActive()) {
            String resetToken = authService.createPasswordResetToken(user.getPassword());

            Map<String, Object> model = Map.of(
                    "recipient", user.makeFullName(),
                    "passwordResetLink", createResetLinkForUser(user, resetToken),
                    "ipAddress", request.getRemoteAddr()
            );

            messagingService.sendEmail(
                    new EmailMessage(
                            new EmailRecipient(user.makeFullName(), user.getEmail()),
                            new EmailBody(templateService.renderView(view("messaging/email/password-reset").addAllObjects(model)), true),
                            "SCTP MIS: Account Password Reset Request"
                    )
            );
        }

        return setSuccessMessage(view("auth/reset-link-sent"), "Password reset link sent to your email on file.");
    }
}

