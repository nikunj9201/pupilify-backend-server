package com.smartschool.api.repository;

import com.smartschool.api.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Get all notes for a specific school
    List<Note> findBySchoolIdAndIsActiveTrue(Long schoolId);

    // Get all notes created by a specific user in a school
    List<Note> findBySchoolIdAndCreatedByIdAndIsActiveTrue(Long schoolId, Long userId);

    // Get a specific note
    Optional<Note> findByIdAndSchoolId(Long noteId, Long schoolId);

    // Check if note exists
    boolean existsByIdAndSchoolId(Long noteId, Long schoolId);
}

