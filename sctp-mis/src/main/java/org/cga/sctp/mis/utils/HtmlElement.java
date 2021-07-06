/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, CGATechnologies
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

package org.cga.sctp.mis.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HtmlElement {
    private String text;
    private final String tagName;
    private final boolean singleTag;
    private boolean textBeforeChildren;
    private final Map<String, Object> attributes;
    private ArrayList<HtmlElement> children;

    public HtmlElement(String tagName, boolean singleTag) {
        this.tagName = tagName;
        this.singleTag = singleTag;
        this.textBeforeChildren = true;
        this.attributes = new LinkedHashMap<>();
    }

    public HtmlElement(String tagName) {
        this(tagName, false);
    }

    public <E extends HtmlElement> E classes(String... names) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String name : names) {
            joiner.add(name);
        }
        attribute("class", joiner.toString());
        return (E) this;
    }

    public <E extends HtmlElement> E addClass(String className) {
        String classList = null;
        if (attributes.containsKey("class")) {
            classList = (String) attributes.get("class");
        }
        if (classList != null) {
            if (!classList.contains(className)) {
                classList += " " + className;
                attributes.put("class", classList);
            }
        } else {
            attributes.put("class", className);
        }
        return (E) this;
    }

    public <E extends HtmlElement> E addClasses(String... classNames) {
        if (classNames.length > 0) {
            for (String cls : classNames) {
                addClass(cls);
            }
        }
        return (E) this;
    }

    public <E extends HtmlElement> E attribute(String name) {
        return attribute(name, name);
    }

    public <E extends HtmlElement> E id(String id) {
        return this.attribute("id", id);
    }

    public <E extends HtmlElement> E name(String name) {
        return this.attribute("name", name);
    }

    public <E extends HtmlElement> E attribute(String name, Object value) {
        this.attributes.put(name, String.valueOf(value));
        return (E) this;
    }

    public <E extends HtmlElement> E addChild(HtmlElement child) {
        if (child != this) {
            if (this.children == null) {
                this.children = new ArrayList<>();
            }
            this.children.add(child);
        }
        return (E) this;
    }

    public <E extends HtmlElement> E text(String text) {
        this.text = text;
        this.textBeforeChildren = this.children == null;
        return (E) this;
    }

    private void build(StringBuilder builder) {
        builder.append('<').append(tagName);

        for (String attrName : attributes.keySet()) {
            builder.append(" ").append(attrName).append('=').append('"').append(attributes.get(attrName)).append('"');
        }

        if (singleTag) {
            builder.append("/>");
        } else {
            builder.append(">");
        }

        if (!singleTag) {
            if (textBeforeChildren) {
                setText(builder);
                buildChildren(builder);
            } else {
                buildChildren(builder);
                setText(builder);
            }
            builder.append("</").append(tagName).append(">");
        }
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        build(builder);
        return builder.toString();
    }

    private void setText(StringBuilder builder) {
        if (text != null) {
            builder.append(text);
        }
    }

    private void buildChildren(StringBuilder sb) {
        if (children != null) {
            for (HtmlElement b : children) {
                b.build(sb);
            }
        }
    }

}
