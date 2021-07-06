package org.cga.sctp.mis.transfers;

import org.cga.sctp.mis.core.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/transfers/calculation")
public class TransferCalculationController extends BaseController {

    @GetMapping
    public ModelAndView list() {
        return view("transfers/calculation/list");
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
