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

package org.cga.sctp.api.config;

import com.google.gson.Gson;
import org.cga.sctp.api.core.AppConstants;
import org.cga.sctp.api.filters.AppVersionFilter;
import org.cga.sctp.api.security.JwtAuthorizationFilterFilter;
import org.cga.sctp.api.security.JwtUtil;
import org.cga.sctp.mobile.MobileApplicationService;
import org.cga.sctp.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Spring security configuration bean.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true, // Allows use of @PreAuthorize(...),
        securedEnabled = true,
        jsr250Enabled = true
)
public class ApiConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OpenApiDocConfiguration openApiDocConfiguration;

    @Autowired
    private MobileApplicationService mobileApplicationService;

    @Value("${api.checkAppVersion:false}")
    private boolean checkAppVersion;

    @Override
    public void configure(WebSecurity web) throws Exception {
        if (openApiDocConfiguration.getEnabled()) {
            web.ignoring()
                    .antMatchers("/v2/api-docs",
                            "/configuration/ui",
                            "/swagger-resources/**",
                            "/configuration/security",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/webjars/**");
        }
    }

    private static final String[] ALLOW_LIST = {
            "/error",
            "/v2/api-doc",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/api-doc",
            "/api-doc/**",
            "/api-doc.yaml",
            "/swagger-ui/**"
    };

    @Autowired
    private Gson gson;

    @Bean
    public AppVersionFilter apiVersionFilter() {
        return new AppVersionFilter(mobileApplicationService, checkAppVersion, gson, ALLOW_LIST);
    }

    @Bean
    public JwtAuthorizationFilterFilter jwtAuthorizationFilterFilter() {
        return new JwtAuthorizationFilterFilter(userService, jwtUtil, ALLOW_LIST);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Disable session cookies
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, AppConstants.AUTHENTICATION_PATH)
                .permitAll(); // Authentication endpoint does not require authentication

        if (openApiDocConfiguration.getEnabled()) {
            http.authorizeRequests()
                    .antMatchers("/api-doc", "/api-doc/**", "/api-doc.yaml", "/swagger-ui", "/swagger-ui/**")
                    .permitAll();
        }

        http.authorizeRequests().anyRequest().authenticated() // Any other request requires authentication
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                .and()
                .addFilterBefore(jwtAuthorizationFilterFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiVersionFilter(), JwtAuthorizationFilterFilter.class);
    }

    CorsConfigurationSource corsConfigurationSource() {
        if (openApiDocConfiguration.getEnabled()) {
            return new UrlBasedCorsConfigurationSource() {{
                registerCorsConfiguration(
                        "/**",
                        new CorsConfiguration() {{
                            setAllowedMethods(List.of(
                                    HttpMethod.DELETE.name(),
                                    HttpMethod.POST.name(),
                                    HttpMethod.GET.name()
                            ));
                        }}.applyPermitDefaultValues()
                );
            }};
        } else {
            return null;
        }
    }
}
