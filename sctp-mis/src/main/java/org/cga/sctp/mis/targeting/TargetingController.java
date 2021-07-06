package org.cga.sctp.mis.targeting;

import org.cga.sctp.mis.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/targeting")
public class TargetingController extends BaseController {

    private static final List<VerificationResult> completedResults = new LinkedList<>();
    private static final List<VerificationResult> approvedResults = new LinkedList<>();

    static {
        for (int i = 0; i < 100; i++) {
            String num = "ML-" + (100000 + i);
            completedResults.add(
                    new VerificationResult(
                            num,
                            String.format(Locale.US, "First%d Last%d", i + 1, i + 1),
                            "Dowa",
                            "Dzoole",
                            "Dzoole",
                            "Kachisa",
                            "Chinkwamba",
                            "" + (6023534 + i),
                            num,
                            "Complete",
                            i + 1
                    )
            );
            approvedResults.add(
                    new VerificationResult(
                            num,
                            String.format(Locale.US, "First%d Last%d", i + 1, i + 1),
                            "Dowa",
                            "Dzoole",
                            "Dzoole",
                            "Kachisa",
                            "Chinkwamba",
                            "" + (6023534 + i),
                            num,
                            "Approved By HQ",
                            i + 1
                    )
            );
        }
    }

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

    @GetMapping("/import")
    public ModelAndView dataImport() {
        return view("targeting/import/history");
    }

    @GetMapping("/import/new")
    public ModelAndView newDataImport() {
        return view("targeting/import/new");
    }

    @GetMapping("/import/review")
    public ModelAndView reviewImport() {
        return view("targeting/import/review");
    }
}
