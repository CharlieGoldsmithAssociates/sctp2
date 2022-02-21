package org.cga.sctp.mis.transfers;

import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.transfers.TransferSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/transfers/calculation")
public class TransferCalculationController extends BaseController {

    @Autowired
    private TransferSessionService transferSessionService;

    @GetMapping
    public ModelAndView list(Pageable pageable) {
        var transferSessions = transferSessionService.findAllActive(pageable);
        return view("transfers/calculation/list")
                .addObject("transferSessions", transferSessions);
    }

    @GetMapping("/new")
    public ModelAndView initiateNew() {
        return view("transfers/calculation/new");
    }

    @GetMapping("/review")
    public ModelAndView reviewInitiation() {
        return view("transfers/calculation/review");
    }

    @GetMapping("/individual-review")
    public ModelAndView reviewInitiation2() {
        return view("transfers/calculation/review2");
    }
}
