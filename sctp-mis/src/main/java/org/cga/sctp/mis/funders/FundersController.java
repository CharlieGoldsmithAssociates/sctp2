package org.cga.sctp.mis.funders;

import org.cga.sctp.funders.Funder;
import org.cga.sctp.funders.FundersService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/funders")
public class FundersController extends BaseController {

    @Autowired
    private FundersService fundersService;

    @GetMapping
    ModelAndView index() {
        return view("funders/list")
                .addObject("funders", fundersService.getAllFunders());
    }

    @GetMapping("/new")
    ModelAndView newFundersForm(@ModelAttribute FunderForm funderForm) {
        return view("funders/new")
                .addObject("options", Booleans.VALUES);
    }

    @GetMapping("/{funder-id}/edit")
    ModelAndView editFunder(@PathVariable("funder-id") Long funderId,
                            @ModelAttribute("funderForm") EditFunderForm funderForm,
                            RedirectAttributes attributes) {
        Funder funder = fundersService.findById(funderId);
        if (funder == null) {
            setDangerFlashMessage("Selected funder does not exist.", attributes);
            return redirect("/funders");
        }
        funderForm.setId(funder.getId());
        funderForm.setName(funder.getName());
        funderForm.setActive(Booleans.of(funder.isActive()));
        return view("/funders/edit")
                .addObject("options", Booleans.VALUES);
    }

    @PostMapping("/update")
    ModelAndView updateFunder(
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute EditFunderForm funderForm,
            BindingResult result,
            RedirectAttributes attributes) {
        Funder funder;
        if (result.hasErrors()) {
            return view("/funders/new")
                    .addObject("options", Booleans.VALUES);
        }

        if ((funder = fundersService.findById(funderForm.getId())) == null) {
            setDangerFlashMessage("Selected funder does not exist.", attributes);
            return redirect("/funders");
        }

        publishGeneralEvent("%s updated funder: Old name=%s, new name %s.",
                username, funder.getName(), funderForm.getName());

        funder.setName(funderForm.getName());
        funder.setCreatedAt(LocalDateTime.now());
        funder.setActive(funderForm.getActive().value);

        fundersService.save(funder);

        setSuccessFlashMessage("Funder updated successfully!", attributes);
        return redirect("/funders");
    }

    @PostMapping
    ModelAndView addFunder(
            @AuthenticationPrincipal String username,
            @Validated @ModelAttribute FunderForm funderForm,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return view("/funders/new")
                    .addObject("options", Booleans.VALUES);
        }

        Funder funder = new Funder();
        funder.setName(funderForm.getName());
        funder.setCreatedAt(LocalDateTime.now());
        funder.setActive(funderForm.getActive().value);

        publishGeneralEvent("%s added new funder %s.", username, funderForm.getName());

        fundersService.save(funder);

        setSuccessFlashMessage("Funder added successfully!", attributes);
        return redirect("/funders");
    }
}
