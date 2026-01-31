package com.medshare.hub.security;

import com.medshare.hub.abac.PolicyDecision;
import com.medshare.hub.abac.PolicyEvaluator;
import com.medshare.hub.abac.attributes.EnvironmentAttributes;
import com.medshare.hub.abac.attributes.ResourceAttributes;
import com.medshare.hub.abac.attributes.SubjectAttributes;
import com.medshare.hub.entity.User;
import com.medshare.hub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AbacInterceptor implements HandlerInterceptor {

    private final PolicyEvaluator policyEvaluator;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();

        // Skip auth for public endpoints (this should match SecurityConfig)
        if (path.startsWith("/api/auth/") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return true; // Let SecurityConfig handle authentication
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return true;
        }

        // Set userId in request attribute for controllers to use
        request.setAttribute("userId", user.getUserId());

        // Attempt to extract patientId from path variables
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long patientId = null;
        if (pathVariables != null && pathVariables.containsKey("patientId")) {
            try {
                patientId = Long.parseLong(pathVariables.get("patientId"));
            } catch (NumberFormatException ignored) {
            }
        }

        // Build ABAC Attributes
        SubjectAttributes subject = SubjectAttributes.builder()
                .userId(user.getUserId())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .certifications(user.getCertifications() != null ? user.getCertifications()
                        : java.util.Collections.emptyList())
                .build();

        ResourceAttributes resource = ResourceAttributes.builder()
                .resourceId(patientId != null ? patientId : 0L)
                .resourceType(path.contains("/records") ? "MEDICAL_RECORD" : "GENERAL")
                .patientId(patientId)
                .build();

        EnvironmentAttributes environment = EnvironmentAttributes.builder()
                .currentTime(java.time.LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .isEmergency(request.getHeader("X-Emergency-Access") != null)
                .build();

        String action = request.getMethod(); // GET -> READ, POST/PUT -> WRITE/CREATE

        // Evaluate Decision
        PolicyDecision decision = policyEvaluator.evaluateAccess(subject, resource, environment, action);

        if (!decision.isPermitted()) {
            log.warn("ABAC DENIAL: User {} denied {} on {} - Reason: {}",
                    email, action, path, decision.getDenyReason());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: " + decision.getDenyReason());
            return false;
        }

        return true;
    }
}
