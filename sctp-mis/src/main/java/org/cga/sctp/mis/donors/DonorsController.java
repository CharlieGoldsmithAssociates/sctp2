package org.cga.sctp.mis.donors;

import org.cga.sctp.mis.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/donors")
public class DonorsController extends BaseController {

    @GetMapping
    ModelAndView index() {
        return view("donors/list");
    }

    @GetMapping("/new")
    ModelAndView newDonor() {
        return view("donors/new");
    }
}
