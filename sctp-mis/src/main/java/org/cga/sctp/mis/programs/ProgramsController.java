package org.cga.sctp.mis.programs;

import org.cga.sctp.mis.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/programs")
public class ProgramsController extends BaseController {

    @GetMapping
    ModelAndView index() {
        return view("programs/list");
    }

    @GetMapping("/new")
    ModelAndView newDonor() {
        return view("programs/new");
    }
}
