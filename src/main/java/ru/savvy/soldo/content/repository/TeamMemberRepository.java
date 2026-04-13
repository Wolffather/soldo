package ru.savvy.soldo.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.content.model.TeamMember;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findAllByOrderBySortOrderAscCreatedAtAsc();
}
