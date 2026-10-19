package com.thebrian31.coworking.controller;

import com.thebrian31.coworking.dto.SpaceRequest;
import com.thebrian31.coworking.dto.SpaceResponse;
import com.thebrian31.coworking.dto.SpaceUpdateRequest;
import com.thebrian31.coworking.service.SpaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<SpaceResponse> findAll() {
        return spaceService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public SpaceResponse findById(@PathVariable Long id) {
        return spaceService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SpaceResponse create(@Valid @RequestBody SpaceRequest request) {
        return spaceService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SpaceResponse update(@PathVariable Long id, @Valid @RequestBody SpaceUpdateRequest request) {
        return spaceService.update(id,
                request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        spaceService.delete(id);
    }
}
