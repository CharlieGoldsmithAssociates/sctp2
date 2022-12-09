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

package org.cga.sctp.mis.transfers.topups;

import org.cga.sctp.mis.core.templating.CategoricalTopUpOptionItem;
import org.cga.sctp.targeting.importation.parameters.ChronicIllness;
import org.cga.sctp.targeting.importation.parameters.Disability;
import org.cga.sctp.targeting.importation.parameters.Orphanhood;
import org.cga.sctp.user.AdminAndStandardAccessOnly;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
@Controller
@RequestMapping("/transfers/topups/categorical/options")
public class CategoricalTopUpOptionsController {

    @GetMapping("disabilities")
    @AdminAndStandardAccessOnly
    public ResponseEntity<List<CategoricalTopUpOptionItem>> getAllDisabilityOptions() {
        return ResponseEntity.ok(Arrays.stream(Disability.values())
                .map(disability -> {
                    String text = isNull(disability.text) ? disability.name() : disability.text;
                    return new CategoricalTopUpOptionItem(disability.code, text);
                })
                .collect(Collectors.toList()));
    }

    @GetMapping("orphanhood")
    @AdminAndStandardAccessOnly
    public ResponseEntity<List<CategoricalTopUpOptionItem>> getAllOrphanhoodOptions() {
        return ResponseEntity.ok(Arrays.stream(Orphanhood.values())
                .map(orphanhood -> {
                    String text = isNull(orphanhood.text) ? orphanhood.name() : orphanhood.text;
                    return new CategoricalTopUpOptionItem(orphanhood.code, text);
                })
                .collect(Collectors.toList()));
    }

    @GetMapping("chronic-illness")
    @AdminAndStandardAccessOnly
    public ResponseEntity<List<CategoricalTopUpOptionItem>> getAllChronicIllnessOptions() {
        return ResponseEntity.ok(Arrays.stream(ChronicIllness.values())
                .map(chronicIllness -> {
                    String text = isNull(chronicIllness.text) ? chronicIllness.name() : chronicIllness.text;
                    return new CategoricalTopUpOptionItem(chronicIllness.code, text);
                })
                .collect(Collectors.toList()));
    }

}
