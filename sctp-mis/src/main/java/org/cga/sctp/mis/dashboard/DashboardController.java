package org.cga.sctp.mis.dashboard;

import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.mis.core.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends BaseController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stats", beneficiaryService.getDashboardStats());
        return "dashboard/index";
    }
}
