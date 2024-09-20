package com.study.spring6restmvc.listeners;

import com.study.spring6restmvc.events.customer.CustomerCreatedEvent;
import com.study.spring6restmvc.events.customer.CustomerDeletedEvent;
import com.study.spring6restmvc.events.customer.CustomerEvent;
import com.study.spring6restmvc.events.customer.CustomerPatchedEvent;
import com.study.spring6restmvc.events.customer.CustomerUpdatedEvent;
import com.study.spring6restmvc.mappers.CustomerMapper;
import com.study.spring6restmvc.repositories.CustomerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.study.spring6restmvc.listeners.EventTypes.CREATED;
import static com.study.spring6restmvc.listeners.EventTypes.DELETED;
import static com.study.spring6restmvc.listeners.EventTypes.PATCHED;
import static com.study.spring6restmvc.listeners.EventTypes.UPDATED;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerAuditRepository customerAuditRepository;
    private final CustomerMapper customerMapper;

    @EventListener
    @Async
    public void listen(CustomerEvent event) {
        String auditEventType;

        switch (event) {
            case CustomerCreatedEvent ignored -> auditEventType = CREATED.name();
            case CustomerUpdatedEvent ignored -> auditEventType = UPDATED.name();
            case CustomerDeletedEvent ignored -> auditEventType = DELETED.name();
            case CustomerPatchedEvent ignored -> auditEventType = PATCHED.name();
            default -> auditEventType = "UNKNOWN";
        }

        var customerAudit = customerMapper.customerToCustomerAudit(event.getCustomer());
        customerAudit.setAuditEventType(auditEventType);

        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            customerAudit.setAuditPrincipalName(event.getAuthentication().getName());
        }

        var savedCustomerAudit = customerAuditRepository.save(customerAudit);
        log.debug("{} Customer Audit saved: id {}", auditEventType, savedCustomerAudit.getAuditId());
    }
}
