package ru.savvy.soldo.content.service;

import ru.savvy.soldo.content.dto.TeamMemberDTO;

import java.util.List;

public interface TeamMemberService {
    List<TeamMemberDTO> getAll();
    TeamMemberDTO create(TeamMemberDTO dto);
    TeamMemberDTO update(Long id, TeamMemberDTO dto);
    void delete(Long id);
}
