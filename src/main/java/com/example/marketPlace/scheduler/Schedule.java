package com.example.marketPlace.scheduler;

import com.example.marketPlace.config.keycloack.KeycloakRoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class Schedule {
    private final ApplicationContext applicationContext;
    private final KeycloakRoleService keycloakRoleService;

    /**
     * اجرا فقط یک بار بعد از بالا آمدن اپلیکیشن
     */
    @PostConstruct
    public void onStartup() {
        syncAuthoritiesWithKeycloak();
    }

    /**
     * اجرای دوره‌ای (اختیاری)
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void syncAuthoritiesWithKeycloak() {

        log.info("🔍 Authority scan started ...");
        Set<String> authorities = new HashSet<>();


        Map<String, Object> restControllers =
                applicationContext.getBeansWithAnnotation(RestController.class);

        Map<String, Object> controllers =
                applicationContext.getBeansWithAnnotation(Controller.class);

        Set<Object> allControllers = new HashSet<>();
        allControllers.addAll(restControllers.values());
        allControllers.addAll(controllers.values());

        for (Object bean : allControllers) {

            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

            for (Method method : targetClass.getDeclaredMethods()) {

                PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
                if (preAuthorize != null) {
                    authorities.addAll(parseAuthorities(preAuthorize.value()));
                }
            }
        }

        authorities.forEach(keycloakRoleService::createClientRoleIfNotExists);

        log.info("✅ Authority sync finished. Total roles: {}", authorities.size());
    }

    private Set<String> parseAuthorities(String expression) {
        Set<String> result = new HashSet<>();

        if (expression.contains("hasAuthority")) {
            String role = expression
                    .replace("hasAuthority(", "")
                    .replace(")", "")
                    .replace("'", "")
                    .trim();
            result.add(role);
        }

        if (expression.contains("hasAnyAuthority")) {
            String roles = expression
                    .replace("hasAnyAuthority(", "")
                    .replace(")", "")
                    .replace("'", "");
            for (String r : roles.split(",")) {
                result.add(r.trim());
            }
        }

        return result;
    }
}
