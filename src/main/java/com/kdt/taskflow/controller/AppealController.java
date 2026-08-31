package com.kdt.taskflow.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.kdt.taskflow.dto.AppealRequest;
import com.kdt.taskflow.dto.AppealResponse;
import com.kdt.taskflow.service.AppealService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/appeals")
public class AppealController {

    private final AppealService appealService;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

    /** POST /api/appeals — Submit a new appeal. Returns 201 Created + header Location. */
    @PostMapping
    public ResponseEntity<AppealResponse> create(@Valid @RequestBody AppealRequest request,
                                                UriComponentsBuilder uriBuilder) {
        AppealResponse created = appealService.create(request);
        URI location = uriBuilder.path("/api/appeals/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/appeals — List/search appeals by username or keyword.
     */
    @GetMapping
    public List<AppealResponse> search(@RequestParam(required = false) String username,
                                       @RequestParam(required = false) String keyword) {
        return appealService.search(username, keyword);
    }

    /** GET /api/appeals/{id} — Get details of a single appeal. Returns 404 if not found. */
    @GetMapping("/{id}")
    public AppealResponse getById(@PathVariable Long id) {
        return appealService.getById(id);
    }

    /** DELETE /api/appeals/{id} — Delete an appeal by id. Returns 204 No Content. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        appealService.delete(id);
    }
}
