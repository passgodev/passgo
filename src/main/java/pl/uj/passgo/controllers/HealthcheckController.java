package pl.uj.passgo.controllers;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("health")
public class HealthcheckController {
    @GetMapping
    public ResponseEntity<Void> getHealthStatus() {
        var cacheControl = CacheControl.noStore();
        return ResponseEntity.noContent().cacheControl(cacheControl).build();
    }
}
