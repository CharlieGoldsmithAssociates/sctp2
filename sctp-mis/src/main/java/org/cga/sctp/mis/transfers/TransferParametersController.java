package org.cga.sctp.mis.transfers;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.program.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/transfers/parameters")
public class TransferParametersController extends BaseController {

    @Autowired
    private ProgramService programService;

    @GetMapping
    public ModelAndView getParametersList() {
        return view("transfers/parameters/index");
    }
}
