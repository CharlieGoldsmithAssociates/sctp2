package org.cga.sctp.mis.targeting;

import org.cga.sctp.beneficiaries.BeneficiaryService;
import org.cga.sctp.beneficiaries.Household;
import org.cga.sctp.beneficiaries.Individual;
import org.cga.sctp.location.Location;
import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.BaseController;
import org.cga.sctp.mis.core.navigation.BreadcrumbPath;
import org.cga.sctp.mis.core.templating.SelectOptionItem;
import org.cga.sctp.program.Program;
import org.cga.sctp.program.ProgramService;
import org.cga.sctp.targeting.*;
import org.cga.sctp.targeting.criteria.Criterion;
import org.cga.sctp.targeting.criteria.HouseholdCountParameters;
import org.cga.sctp.user.AdminAccessOnly;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/verification")
public class EligibilityVerificationController extends BaseController {

    @Autowired
    private TargetingService targetingService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @AdminAndStandardAccessOnly
    @GetMapping
    public ModelAndView verification(Pageable pageable) {
        Page<EligibilityVerificationSessionView> verificationList
                = targetingService.getVerificationSessionViews(pageable);
        return view("targeting/verification/history", "verifications", verificationList);
    }


    @AdminAccessOnly
    @GetMapping(value = "/get-new-session-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<SelectOptionItem>>> getNewVerificationSessionData() {
        List<Program> programs = programService.getActivePrograms();
        List<Criterion> criteria = targetingService.getActiveTargetingCriteria();
        HashMap<String, List<SelectOptionItem>> data = new HashMap<>() {{
            put("programs", programs.stream().map(program -> new SelectOptionItem(program.getId(), program.getName())).toList());
            put("criteria", criteria.stream().map(criterion -> new SelectOptionItem(criterion.getId(), criterion.getName())).toList());
        }};
        return ResponseEntity.ok(data);
    }

    @AdminAccessOnly
    @GetMapping("/new")
    @BreadcrumbPath(link = "/new", title = "New Eligibility Verification Session")
    public ModelAndView newVerificationSession(@ModelAttribute("form") NewVerificationSessionForm form) {
        return view("targeting/verification/new");
    }

    @AdminAccessOnly
    @PostMapping(value = "/count-matching-households", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> countHouseholdsMatchingCriterionFilters(
            @Valid @RequestBody HouseholdCountParameters parameters,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        final Criterion criterion = targetingService.findCriterionById(parameters.getCriterionId());
        if (criterion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(targetingService.countHouseholdsMatchingCriterionFilters(parameters, criterion));
    }

    @AdminAccessOnly
    @PostMapping
    public ResponseEntity<Long> create(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @RequestBody NewVerificationSessionForm form,
            BindingResult result) {

        if (result.hasErrors()) {
            LOG.error("request contains errors {}", result.getAllErrors());
            return ResponseEntity.badRequest().build();
        }

        Program program = programService.getProgramById(form.getProgramId());
        Criterion criterion = targetingService.getActiveTargetingCriterionById(form.getCriterionId());
        Location district = locationService.findActiveLocationByCodeAndType(form.getDistrictCode(), LocationType.SUBNATIONAL1);

        if (program == null) {
            LOG.warn("program ID not found");
            return ResponseEntity.badRequest().build();
        }

        if (district == null) {
            LOG.warn("Cannot find district");
            return ResponseEntity.badRequest().build();
        }

        if (criterion == null) {
            LOG.warn("Selected criteria cannot be found.");
            return ResponseEntity.badRequest().build();
        }

        if (!criterion.isApplicable()) {
            LOG.warn("Selected criterion cannot be used because it does not contain filters.");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        if (!locationService.verifyClustersBelongToDistrict(district, form.getClusterCodes())) {
            LOG.warn("Found clusters that do not belong to selected district");
            return ResponseEntity.badRequest().build();
        }

        EligibilityVerificationSession session = new EligibilityVerificationSession();
        session.setHouseholds(0L);
        session.setUserId(user.id());
        session.setTaCode(0L);
        session.setProgramId(program.getId());
        session.setClusters(form.getClusterCodes());
        session.setCriterionId(criterion.getId());
        session.setCreatedAt(ZonedDateTime.now());
        session.setDistrictCode(district.getCode());
        session.setStatus(EligibilityVerificationSessionBase.Status.Review);

        // Run
        targetingService.addEligibilityVerificationSession(session, criterion, user.id());

        return ResponseEntity.ok(session.getId());
    }

    @AdminAndStandardAccessOnly
    @GetMapping("/review")
    @BreadcrumbPath(link = "/review", title = "Eligibility Verification Review")
    public ModelAndView reviewEligibilityList(@RequestParam Long id, RedirectAttributes attributes, Pageable pageable) {
        EligibilityVerificationSessionView verificationSessionView = targetingService.findVerificationViewById(id);
        if (verificationSessionView == null) {
            setDangerFlashMessage("Cannot find verification session.", attributes);
            return redirect("/verification");
        }
        Page<EligibleHousehold> households = targetingService.getEligibleHouseholds(verificationSessionView, pageable);
        return view("targeting/verification/review", "households", households)
                .addObject("verification", verificationSessionView);
    }

    @AdminAndStandardAccessOnly
    @GetMapping("/export/with-member-details")
    public ResponseEntity<Resource> exportEligibleHouseholdsWithMemberDetails(@RequestParam("session-id") long id) {
        EligibilityVerificationSessionView session = targetingService.findVerificationViewById(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = targetingService.exportEligibleHouseholdsWithMemberDetails(session);
        if (resource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", format("filename=%s", resource.getFilename()))
                .body(resource);
    }


    @AdminAndStandardAccessOnly
    @GetMapping("/hh-composition")
    @BreadcrumbPath(link = "/hh-composition", title = "Household Composition")
    public ModelAndView householdComposition(
            @RequestParam Long session,
            @RequestParam(name = "household_id") Long householdId,
            RedirectAttributes attributes) {
        Household household;
        List<Individual> individuals;
        EligibilityVerificationSessionView verificationSessionView;

        verificationSessionView = targetingService.findVerificationViewById(session);

        if (verificationSessionView == null) {
            setDangerFlashMessage("Cannot find verification session.", attributes);
            return redirect("/verification");
        }

        if ((household = beneficiaryService.findHouseholdById(householdId)) == null) {
            setDangerFlashMessage("Cannot find household", attributes);
            return redirect("/verification/review?id=" + session);
        }

        individuals = beneficiaryService.getHouseholdMembers(household.getHouseholdId());

        return view("targeting/verification/composition")
                .addObject("individuals", individuals)
                .addObject("verification", verificationSessionView)
                .addObject("household", household);
    }

    @AdminAccessOnly
    @PostMapping("/close")
    ModelAndView closeSession(
            @AuthenticatedUserDetails AuthenticatedUser user,
            @Valid @ModelAttribute("form") CloseVerificationSessionForm form,
            BindingResult result,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            setDangerFlashMessage("Something went wrong. Please try again.", attributes);
            return redirect("/verification");
        }

        EligibilityVerificationSession session = targetingService.getVerificationSessionById(form.getId());
        if (session == null) {
            setDangerFlashMessage("Cannot find pre-eligibility verification session.", attributes);
            return redirect("/verification");
        }

        if (!session.isOpen()) {
            setDangerFlashMessage("Cannot close this pre-eligibility verification session because it is already closed.", attributes);
            return redirect("/verification");
        }

        targetingService.closeVerificationSession(session, form.getDestination(), user.id());

        if (session.getHouseholds() == 0) {
            setWarningFlashMessage("Pre-Eligibility verification session closed. However, there were no households that matched the targeting criteria.", attributes);
        } else {
            setSuccessFlashMessage(format("Pre-Eligibility verification closed and sent to %s",
                    form.getDestination()), attributes);
        }
        publishGeneralEvent("%s closed pre-eligibility verification session with id %d",
                user.username(), session.getId());

        return redirect("/verification");
    }
}
