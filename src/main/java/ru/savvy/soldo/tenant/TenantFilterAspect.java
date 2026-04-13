package ru.savvy.soldo.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager em;

    @Around("@within(org.springframework.stereotype.Service) && within(ru.savvy.soldo..*)")
    public Object applyTenantFilter(ProceedingJoinPoint pjp) throws Throwable {
        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            return pjp.proceed();
        }

        Session session;
        try {
            session = em.unwrap(Session.class);
        } catch (Exception e) {
            log.debug("No Hibernate session available, skipping tenant filter");
            return pjp.proceed();
        }

        boolean weEnabled = session.getEnabledFilter("tenantFilter") == null;
        if (weEnabled) {
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }

        try {
            return pjp.proceed();
        } finally {
            if (weEnabled) {
                try {
                    session.disableFilter("tenantFilter");
                } catch (Exception ignored) {}
            }
        }
    }
}
