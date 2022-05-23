package org.cga.sctp.mis.core;

import org.cga.sctp.core.BaseComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class BaseController extends BaseComponent {
    private static final String MESSAGE_SUCCESS = "successMessage";
    private static final String MESSAGE_WARNING = "warningMessage";
    private static final String MESSAGE_DANGER = "dangerMessage";
    private static final String MESSAGE_INFO = "infoMessage";

    protected final Logger logger;

    protected BaseController() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @SuppressWarnings("unchecked")
    protected final <T> T getAuthenticationDetails(Authentication authentication) {
        return (T) authentication.getDetails();
    }

    protected final ModelAndView view(String viewName) {
        // Fixes error during view resolution. This has been observed to occur primarily on Amazon Corretto JVM
        if (viewName.startsWith("/")) {
            viewName = viewName.substring(1);
        }
        return new ModelAndView(viewName);
    }

    protected final ModelAndView withSuccessMessage(String view, String successMessage) {
        return view(view).addObject(MESSAGE_SUCCESS, successMessage);
    }

    protected final ModelAndView withDangerMessage(String view, String dangerMessage) {
        return view(view).addObject(MESSAGE_DANGER, dangerMessage);
    }

    protected final ModelAndView view(String viewName, String objectName, Object object) {
        return view(viewName).addObject(objectName, object);
    }

    protected final ModelAndView redirect(String url) {
        return view("redirect:" + url);
    }

    private void addFlashMessage(String name, String message, RedirectAttributes attributes) {
        attributes.addFlashAttribute(name, message);
    }

    protected final void setSuccessFlashMessage(String message, RedirectAttributes attributes) {
        addFlashMessage(MESSAGE_SUCCESS, message, attributes);
    }

    protected final void setDangerFlashMessage(String message, RedirectAttributes attributes) {
        addFlashMessage(MESSAGE_DANGER, message, attributes);
    }

    protected final void setWarningFlashMessage(String message, RedirectAttributes attributes) {
        addFlashMessage(MESSAGE_WARNING, message, attributes);
    }

    protected final void setInfoFlashMessage(String message, RedirectAttributes attributes) {
        addFlashMessage(MESSAGE_INFO, message, attributes);
    }

    protected final void setMessage(Model model, String name, String message) {
        model.addAttribute(name, message);
    }

    protected final ModelAndView setMessage(ModelAndView modelAndView, String name, String message) {
        return modelAndView.addObject(name, message);
    }

    protected final void setDangerMessage(Model model, String message) {
        setMessage(model, MESSAGE_DANGER, message);
    }

    protected final ModelAndView setDangerMessage(ModelAndView modelAndView, String message) {
        return setMessage(modelAndView, MESSAGE_DANGER, message);
    }

    protected final ModelAndView setSuccessMessage(ModelAndView modelAndView, String message) {
        return setMessage(modelAndView, MESSAGE_SUCCESS, message);
    }

    protected final String redirectWithSuccessMessage(String url, String message, RedirectAttributes attributes) {
        setSuccessFlashMessage(message, attributes);
        return "redirect:" + url;
    }

    protected final String redirectWithDangerMessage(String url, String message, RedirectAttributes attributes) {
        setDangerFlashMessage(message, attributes);
        return "redirect:" + url;
    }

    protected final ModelAndView redirectWithDangerMessageModelAndView(String url, String message, RedirectAttributes attributes) {
        setDangerFlashMessage(message, attributes);
        return view("redirect:" + url);
    }
}
