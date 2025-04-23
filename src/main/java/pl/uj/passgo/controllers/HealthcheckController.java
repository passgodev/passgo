package pl.uj.passgo.controllers;

import org.springframework.context.annotation.Role;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/health")
public class HealthcheckController {
    @PreAuthorize("hasAuthority('READ_HEALTH')")
    @GetMapping
    public ResponseEntity<Void> getHealthStatus() {
        var cacheControl = CacheControl.noStore();
        return ResponseEntity.noContent().cacheControl(cacheControl).build();
    }
}
