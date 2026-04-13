package ru.savvy.soldo.content.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.content.dto.TeamMemberDTO;
import ru.savvy.soldo.content.model.TeamMember;
import ru.savvy.soldo.content.repository.TeamMemberRepository;
import ru.savvy.soldo.content.service.TeamMemberService;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.shared.service.FileStorageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository repository;
    private final FileStorageService fileStorageService;

    @Override
    public List<TeamMemberDTO> getAll() {
        return repository.findAllByOrderBySortOrderAscCreatedAtAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public TeamMemberDTO create(TeamMemberDTO dto) {
        TeamMember entity = TeamMember.builder()
                .name(dto.getName())
                .role(dto.getRole())
                .initials(dto.getInitials())
                .bio(dto.getBio())
                .quote(dto.getQuote())
                .photoUrl(dto.getPhotoUrl())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        return toDto(repository.save(entity));
    }

    @Override
    public TeamMemberDTO update(Long id, TeamMemberDTO dto) {
        TeamMember entity = EntityFinder.findOrThrow(repository.findById(id), "Участник не найден: " + id);
        entity.setName(dto.getName());
        entity.setRole(dto.getRole());
        entity.setInitials(dto.getInitials());
        entity.setBio(dto.getBio());
        entity.setQuote(dto.getQuote());
        entity.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        return toDto(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        TeamMember entity = EntityFinder.findOrThrow(repository.findById(id), "Участник не найден: " + id);
        // delete photo file if stored locally
        if (entity.getPhotoUrl() != null && entity.getPhotoUrl().startsWith("/api/files/")) {
            fileStorageService.delete(entity.getPhotoUrl());
        }
        repository.deleteById(id);
    }

    private TeamMemberDTO toDto(TeamMember e) {
        return TeamMemberDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .role(e.getRole())
                .initials(e.getInitials())
                .bio(e.getBio())
                .quote(e.getQuote())
                .photoUrl(e.getPhotoUrl())
                .sortOrder(e.getSortOrder())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null)
                .build();
    }
}
