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

package org.cga.sctp.mis.core.templating.functions;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.cga.sctp.mis.core.templating.PebbleFunctionImpl;
import org.cga.sctp.mis.utils.HtmlElement;
import org.cga.sctp.utils.UrlUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class Pagination extends PebbleFunctionImpl {
    public Pagination() {
        super("paginate", List.of("page", "request"));
    }

    @Override
    public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
        final Object page = args.get("page");
        final HttpServletRequest request = (HttpServletRequest) args.get("request");

        if (page instanceof Page<?> p) {
            return renderPage(p, request);
        } else if (page instanceof Slice<?> slice) {
            return renderSlice(slice, request);
        }
        throw new IllegalArgumentException("Only Page or Slice is allowed.");
    }

    private String renderSlice(Slice<?> slice, HttpServletRequest request) {
        HtmlElement prev = new HtmlElement("a").addClass("pagination-previous").text("Previous Page");
        HtmlElement next = new HtmlElement("a").addClass("pagination-next").text("Next Page");

        HtmlElement nav = new HtmlElement("nav")
                .addClasses("pagination")
                .attribute("role", "navigation")
                .attribute("aria-label", "pagination");

        if (slice.hasContent()) {
            if (!slice.hasPrevious()) {
                prev.attribute("disabled");
            } else {
                prev.attribute("href", makeLink(request, slice.getNumber() - 1, slice.getSize(), slice.getSort()));
            }
            if (!slice.hasNext()) {
                next.attribute("disabled");
            } else {
                next.attribute("href", makeLink(request, slice.getNumber() + 1, slice.getSize(), slice.getSort()));
            }
        } else {
            next.attribute("disabled");
            prev.attribute("disabled");
        }
        nav.addChild(prev).addChild(next);
        return nav.build();
    }

    private String renderPage(Page<?> page, HttpServletRequest request) {
        return renderSlice(page, request);
    }

    private String makeLink(HttpServletRequest request, int page, int size, Sort sort) {
        try {
            StringBuffer url = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null) {
                url.append('?').append(queryString);
            }
            UrlUtil urlUtil = new UrlUtil(url.toString());
            urlUtil.removeParameter("sort")
                    .replaceSingleParameter("page", page)
                    .setParameter("size", size);
            for (Sort.Order order : sort) {
                urlUtil.addParameter("sort", order.getProperty() + "," + order.getDirection().name().toLowerCase());
            }
            return urlUtil.build();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Shiit!", e);
        }
    }
}