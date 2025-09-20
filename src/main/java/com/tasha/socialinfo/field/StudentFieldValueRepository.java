package com.tasha.socialinfo.field;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentFieldValueRepository extends JpaRepository<StudentFieldValue, StudentFieldId> {
    List<StudentFieldValue> findByStudent_Id(Long studentId);
    @Modifying
    @Query("DELETE FROM StudentFieldValue sfv WHERE sfv.student.id IN :studentIds")
    void deleteAllByStudentIds(@Param("studentIds") List<Long> studentIds);
}
