package ru.savvy.soldo.content.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.content.dto.TeamMemberDTO;
import ru.savvy.soldo.content.service.TeamMemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    /** Public: список участников команды */
    @GetMapping("/public/team")
    public ResponseEntity<List<TeamMemberDTO>> getAll() {
        return ResponseEntity.ok(teamMemberService.getAll());
    }

    /** Admin: создать участника */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/team")
    public ResponseEntity<TeamMemberDTO> create(@Valid @RequestBody TeamMemberDTO dto) {
        return ResponseEntity.ok(teamMemberService.create(dto));
    }

    /** Admin: обновить участника */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/team/{id}")
    public ResponseEntity<TeamMemberDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TeamMemberDTO dto) {
        return ResponseEntity.ok(teamMemberService.update(id, dto));
    }

    /** Admin: удалить участника */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/team/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
