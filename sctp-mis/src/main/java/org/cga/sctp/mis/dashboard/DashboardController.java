package org.cga.sctp.mis.dashboard;

import org.cga.sctp.mis.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends BaseController {

    @GetMapping("/")
    public String home() {
        return "dashboard/index";
    }
}
