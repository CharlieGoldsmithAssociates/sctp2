package org.cga.sctp.mis.targeting;

import org.cga.sctp.mis.core.SecuredBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/targeting")
public class TargetingController extends SecuredBaseController {

    private static final List<VerificationResult> completedResults = new LinkedList<>();
    private static final List<VerificationResult> approvedResults = new LinkedList<>();

    @GetMapping("/verification")
    public ModelAndView verification() {
        return view("targeting/verification/history", "results", completedResults);
    }

    /*@GetMapping("/verification")
    public ModelAndView verification() {
        return view("targeting/verification", "results", completedResults);
    }*/

    @GetMapping("/verification/new")
    public ModelAndView newVerificationSession() {
        return view("targeting/verification/new");
    }

    @GetMapping("/verification/review")
    public ModelAndView reviewEligibilityList() {
        return view("targeting/verification/review", "results", completedResults);
    }
}
