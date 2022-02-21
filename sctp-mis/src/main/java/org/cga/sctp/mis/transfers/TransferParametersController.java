package org.cga.sctp.mis.transfers;

import org.cga.sctp.mis.core.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/transfers/parameters")
public class TransferParametersController extends BaseController {
    @GetMapping
    public ModelAndView getParametersList() {
        return view("transfers/parameters/index");
    }
}
