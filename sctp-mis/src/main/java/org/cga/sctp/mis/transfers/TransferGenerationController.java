package org.cga.sctp.mis.transfers;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.targeting.VerificationResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/transfers/generation")
public class TransferGenerationController extends BaseController {

    private static final List<VerificationResult> completedResults = new LinkedList<>();

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
        }
    }

    @GetMapping
    public ModelAndView list() {
        return view("transfers/generation/list");
    }

    @GetMapping("/new")
    public ModelAndView initiateNew() {
        return view("transfers/generation/new");
    }

    @GetMapping("/review")
    public ModelAndView reviewInitiation() {
        return view("transfers/generation/review", "results", completedResults);
    }

    @GetMapping("/individual-review")
    public ModelAndView individualReview() {
        return view("transfers/generation/review2", "results", completedResults);
    }
}
