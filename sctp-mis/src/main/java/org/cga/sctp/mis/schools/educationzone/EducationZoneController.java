/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, CGATechnologies
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.cga.sctp.mis.schools.educationzone;

import org.cga.sctp.location.LocationService;
import org.cga.sctp.location.LocationType;
import org.cga.sctp.mis.core.SecuredBaseController;
import org.cga.sctp.mis.core.templating.Booleans;
import org.cga.sctp.schools.educationzone.EducationZone;
import org.cga.sctp.schools.educationzone.EducationZoneRepository;
import org.cga.sctp.schools.educationzone.EducationZoneView;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.cga.sctp.user.AuthenticatedUser;
import org.cga.sctp.user.AuthenticatedUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.cga.sctp.mis.location.LocationCodeUtil.toSelectOptions;

@Controller
@RequestMapping("/schools/education-zones")
public class EducationZoneController extends SecuredBaseController {

    @Autowired
    private EducationZoneRepository educationZoneRepository;

    @Autowired
    private LocationService locationService;

    @GetMapping
    @AdminAndStandardAccessOnly
    public ModelAndView getIndex(Pageable pageable) {
        Page<EducationZoneView> educationZones = educationZoneRepository.fetchAllZonesPaged(pageable);
        return view("schools/education-zones/list")
                .addObject("educationZones", educationZones);
    }

    @GetMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView getCreate() {
        return view("schools/education-zones/new")
                .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()))
                .addObject("traditionalAuthorities", locationService.getActiveByType(LocationType.SUBNATIONAL2))
                .addObject("booleans", Booleans.VALUES);
    }

    @PostMapping("/new")
    @AdminAndStandardAccessOnly
    public ModelAndView postCreate(@AuthenticatedUserDetails AuthenticatedUser user,
                                   @ModelAttribute EducationZoneForm form,
                                   BindingResult result,
                                   RedirectAttributes attributes) {

        if (result.hasErrors()) {
            return withDangerMessage("schools/education-zones/new", "Please fix form errors")
                    .addObject(form)
                    .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()))
                    // T/As will be loaded dynamically .addObject("traditionalAuthorities", locationService.getActiveByType(LocationType.SUBNATIONAL2))
                    .addObject("booleans", Booleans.VALUES);
        }

        EducationZone newEducationZone = new EducationZone();

        newEducationZone.setAltName(form.getAltName());
        newEducationZone.setCode(form.getCode());
        newEducationZone.setDistrictCode(form.getDistrictCode());
        newEducationZone.setId(form.getId());
        newEducationZone.setName(form.getName());
        newEducationZone.setTaCode(form.getTaCode());
        newEducationZone.setActive(form.isActive().value);
        newEducationZone.setCreatedAt(ZonedDateTime.now());
        newEducationZone.setUpdatedAt(ZonedDateTime.now());

        educationZoneRepository.save(newEducationZone);

        publishGeneralEvent("User %s created a new EducationZone id=%s", user.username(), newEducationZone.getId());

        return redirect("/schools/education-zones");
    }

    @GetMapping("/{education-zone-id}/edit")
    @AdminAndStandardAccessOnly
    public ModelAndView getEdit(@PathVariable("education-zone-id") Long id,
                                @ModelAttribute("form") EducationZoneForm form,
                                RedirectAttributes attributes) {
        Optional<EducationZoneView> educationZoneOptional = educationZoneRepository.findByIdAsView(id);

        if (educationZoneOptional.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/schools/education-zones", "Education Zone with given ID does not exist", attributes);
        }

        EducationZoneView educationZone = educationZoneOptional.get();
        form.setCode(educationZone.getCode());
        form.setDistrictCode(educationZone.getDistrictCode());
        form.setId(educationZone.getId());
        form.setTaCode(educationZone.getTaCode());
        form.setName(educationZone.getName());
        form.setAltName(educationZone.getAltName());
        form.setActive(Booleans.of(educationZone.getActive()));


        return view("schools/education-zones/edit")
                .addObject(form)
                .addObject("educationZone", educationZone)
                .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()))
                .addObject("taCodes", toSelectOptions(locationService.getLocationCodesByParent(educationZone.getDistrictCode())))
                .addObject("booleans", Booleans.VALUES);
    }

    @PostMapping("/{education-zone-id}/edit")
    @AdminAndStandardAccessOnly
    public ModelAndView postEdit(@PathVariable("education-zone-id") Long id,
                                 @AuthenticatedUserDetails AuthenticatedUser user,
                                 @ModelAttribute("form") EducationZoneForm form,
                                 BindingResult result,
                                 RedirectAttributes attributes) {
        Optional<EducationZone> educationZoneOptional = educationZoneRepository.findById(id);

        if (educationZoneOptional.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/schools/education-zones", "Education Zone with given ID does not exist", attributes);
        }
        EducationZone entity = educationZoneOptional.get();
        if (result.hasErrors()) {
            return withDangerMessage("schools/education-zones/edit", "Please fix form errors")
                    .addObject(form)
                    .addObject("educationZone", entity)
                    .addObject("districts", toSelectOptions(locationService.getActiveDistrictCodes()))
                    .addObject("traditionalAuthorities", locationService.getActiveByType(LocationType.SUBNATIONAL2))
                    .addObject("booleans", Booleans.VALUES);
        }


        entity.setAltName(form.getAltName());
        entity.setCode(form.getCode());
        entity.setDistrictCode(form.getDistrictCode());
        entity.setId(form.getId());
        entity.setName(form.getName());
        entity.setTaCode(form.getTaCode());
        entity.setActive(form.isActive().value);
        entity.setCreatedAt(ZonedDateTime.now());
        entity.setUpdatedAt(ZonedDateTime.now());

        educationZoneRepository.save(entity);

        publishGeneralEvent("User %s updated existing EducationZone id=%s", user.username(), entity.getId());

        return redirect("/schools/education-zones");
    }

    @GetMapping("/{education-zone-id}/delete")
    @AdminAndStandardAccessOnly
    public ModelAndView getDelete(@PathVariable("education-zone-id") Long id, RedirectAttributes attributes) {

        Optional<EducationZoneView> educationZone = educationZoneRepository.findByIdAsView(id);
        if (educationZone.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/schools/education-zones", "Education Zone with given ID does not exist", attributes);
        }
        return view("schools/education-zones/delete")
                .addObject("educationZone", educationZone.get());
    }

    @PostMapping("/{education-zone-id}/delete")
    @AdminAndStandardAccessOnly
    public ModelAndView postDelete(@AuthenticatedUserDetails AuthenticatedUser user,
                                   @PathVariable("education-zone-id") Long id,
                                   RedirectAttributes attributes) {
        Optional<EducationZone> educationZone = educationZoneRepository.findById(id);
        if (educationZone.isEmpty()) {
            return redirectWithDangerMessageModelAndView("/schools/education-zones", "Education Zone with given ID does not exist", attributes);
        }

        try {
            educationZone.get().setActive(false);
            educationZoneRepository.save(educationZone.get());
            publishGeneralEvent("User %s de-activated existing EducationZone id=%s name=%s", user.username(), id, educationZone.get().getName());
            return view(redirectWithSuccessMessage("/schools/education-zones", "Education Zone with given ID de-activated", attributes));
        } catch (Exception e) {
            LOG.error("Failed to de-activate EducationZone", e);
            return redirectWithDangerMessageModelAndView("/schools/education-zones", "Education Zone with given ID could not be de-activated", attributes);
        }
    }
}
