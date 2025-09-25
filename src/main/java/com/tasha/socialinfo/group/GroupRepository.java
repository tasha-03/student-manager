package com.tasha.socialinfo.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    boolean existsByCode(String code);
    List<Group> findByCategoryId(Long categoryId);
}
