package org.cga.sctp.mis.programs;

import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.program.ProgrammeType;
import org.cga.sctp.user.RoleConstants;
import org.cga.sctp.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/programs")
public class ProgramsController extends BaseController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProgramService programService;

    @GetMapping
    @Secured({RoleConstants.ROLE_ADMINISTRATOR, RoleConstants.ROLE_STANDARD})
    ModelAndView index() {
        return view("programs/list")
                .addObject("programs", programService.getProgramInfo());
    }

    @GetMapping("/new")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    ModelAndView newProgram(@ModelAttribute("programForm") ProgramForm programForm) {
        return view("programs/new")
                .addObject("booleans", Booleans.VALUES)
                .addObject("locations", locationService.getActiveCountries());
    }

    @GetMapping("/{program-id}/edit")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    ModelAndView editProgram(
            @PathVariable("program-id") Long programId,
            @ModelAttribute("editForm") EditProgramForm editForm,
            RedirectAttributes attributes) {

        Program program = programService.getProgramById(programId);

        if (program == null) {
            setDangerFlashMessage("Selected program does not exist.", attributes);
            return redirect("/programs");
        }

        editForm.setId(programId);
        editForm.setName(program.getName());
        editForm.setEndDate(program.getEndDate());
        editForm.setLocation(program.getLocation());
        editForm.setStartDate(program.getStartDate());
        editForm.setActive(Booleans.of(program.isActive()));

        return view("programs/edit")
                .addObject("booleans", Booleans.VALUES)
                .addObject("locations", locationService.getActiveCountries());
    }

    @PostMapping
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    ModelAndView addProgram(
            @AuthenticationPrincipal String username,
            HttpServletRequest request,
            @Validated @ModelAttribute("programForm") ProgramForm programForm,
            BindingResult result,
            RedirectAttributes attributes) {

        Program program;
        Location location;

        if (result.hasErrors()) {
            return view("/programs/new")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("locations", locationService.getActiveCountries());
        }

        if ((location = locationService.findById(programForm.getLocation())) == null) {
            return withDangerMessage("/programs/new", "Selected location does not exist")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("locations", locationService.getActiveCountries());
        }

        if (programForm.getEndDate() != null) {
            if (DateUtils.isDateAfter(programForm.getStartDate(), programForm.getEndDate())) {
                return withDangerMessage("/programs/new", "Start date must be before end date.")
                        .addObject("booleans", Booleans.VALUES)
                        .addObject("locations", locationService.getActiveCountries());
            }
        }

        program = new Program();
        program.setLocation(location.getId());
        program.setName(programForm.getName());
        program.setCreatedAt(LocalDateTime.now());
        program.setEndDate(programForm.getEndDate());
        program.setActive(programForm.getActive().value);
        program.setStartDate(programForm.getStartDate());
        program.setCode(format("PG%X", System.currentTimeMillis()));
        program.setProgrammeType(ProgrammeType.PROGRAMME);

        programService.save(program);

        publishGeneralEvent("%s added new program %s.", username, programForm.getName());

        setSuccessFlashMessage("Program successfully added!", attributes);
        return redirect("/programs");
    }

    @PostMapping("/update")
    @Secured({RoleConstants.ROLE_ADMINISTRATOR})
    ModelAndView updateProgram(
            @AuthenticationPrincipal String username,
            HttpServletRequest request,
            @Validated @ModelAttribute("programForm") EditProgramForm programForm,
            BindingResult result,
            RedirectAttributes attributes) {

        final Program program;
        Location location;

        if (result.hasErrors()) {
            return view("/programs/edit")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("locations", locationService.getActiveCountries());
        }

        if ((program = programService.getProgramById(programForm.getId())) == null) {
            setDangerFlashMessage("Selected programme does not exist.", attributes);
            return redirect("/programs");
        }

        if ((location = locationService.findById(programForm.getLocation())) == null) {
            return withDangerMessage("/programs/edit", "Selected location does not exist")
                    .addObject("booleans", Booleans.VALUES)
                    .addObject("locations", locationService.getActiveCountries());
        }

        if (programForm.getEndDate() != null) {
            if (DateUtils.isDateAfter(programForm.getStartDate(), programForm.getEndDate())) {
                return withDangerMessage("/programs/edit", "Start date must be before end date.")
                        .addObject("booleans", Booleans.VALUES)
                        .addObject("locations", locationService.getActiveCountries());
            }
        }

        publishGeneralEvent("%s updated program (old name=%s, new name=%s).",
                username, program.getName(), programForm.getName());

        program.setLocation(location.getId());
        program.setName(programForm.getName());
        program.setEndDate(programForm.getEndDate());
        program.setActive(programForm.getActive().value);
        program.setStartDate(programForm.getStartDate());

        programService.save(program);

        setSuccessFlashMessage("Program successfully updated!", attributes);
        return redirect("/programs");
    }


}
