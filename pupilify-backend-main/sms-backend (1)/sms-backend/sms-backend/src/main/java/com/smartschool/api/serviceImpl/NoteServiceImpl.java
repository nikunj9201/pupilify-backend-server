package com.smartschool.api.serviceImpl;

import com.smartschool.api.entity.Note;
import com.smartschool.api.entity.School;
import com.smartschool.api.entity.User;
import com.smartschool.api.repository.NoteRepository;
import com.smartschool.api.repository.SchoolRepository;
import com.smartschool.api.repository.UserRepository;
import com.smartschool.api.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Note createNote(Note note, Long schoolId, String username) {
        log.info("Creating note for school: {} by user: {}", schoolId, username);

        // Verify school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found with id: " + schoolId));

        // Verify user exists and belongs to this school
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("User does not belong to this school");
        }

        // Set the school and user
        note.setSchool(school);
        note.setCreatedBy(user);

        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            throw new RuntimeException("Note title is required");
        }

        if (note.getContent() == null || note.getContent().isEmpty()) {
            throw new RuntimeException("Note content is required");
        }

        Note savedNote = noteRepository.save(note);
        log.info("Note created successfully with id: {}", savedNote.getId());
        return savedNote;
    }

    @Override
    public List<Note> getNotesBySchool(Long schoolId, String username) {
        log.info("Fetching notes for school: {} by user: {}", schoolId, username);

        // Verify school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new RuntimeException("School not found with id: " + schoolId);
        }

        // Verify user belongs to this school
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("User does not belong to this school");
        }

        return noteRepository.findBySchoolIdAndIsActiveTrue(schoolId);
    }

    @Override
    public Optional<Note> getNoteById(Long noteId, Long schoolId, String username) {
        log.info("Fetching note: {} for school: {}", noteId, schoolId);

        // Verify user belongs to this school
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("User does not belong to this school");
        }

        return noteRepository.findByIdAndSchoolId(noteId, schoolId);
    }

    @Override
    public Note updateNote(Long noteId, Note noteDetails, Long schoolId, String username) {
        log.info("Updating note: {} for school: {}", noteId, schoolId);

        // Verify user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("User does not belong to this school");
        }

        // Get the note
        Note note = noteRepository.findByIdAndSchoolId(noteId, schoolId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Only allow the creator to edit
        if (!note.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the creator can edit this note");
        }

        // Update fields
        if (noteDetails.getTitle() != null && !noteDetails.getTitle().isEmpty()) {
            note.setTitle(noteDetails.getTitle());
        }

        if (noteDetails.getContent() != null && !noteDetails.getContent().isEmpty()) {
            note.setContent(noteDetails.getContent());
        }

        Note updatedNote = noteRepository.save(note);
        log.info("Note updated successfully with id: {}", updatedNote.getId());
        return updatedNote;
    }

    @Override
    public void deleteNote(Long noteId, Long schoolId, String username) {
        log.info("Soft deleting note: {} for school: {}", noteId, schoolId);

        // Verify user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("User does not belong to this school");
        }

        // Get the note
        Note note = noteRepository.findByIdAndSchoolId(noteId, schoolId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Only allow the creator to delete
        if (!note.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the creator can delete this note");
        }

        // Soft delete
        note.setIsActive(false);
        noteRepository.save(note);
        log.info("Note soft deleted successfully");
    }

    @Override
    public void hardDeleteNote(Long noteId, Long schoolId, String username) {
        log.info("Hard deleting note: {} for school: {}", noteId, schoolId);

        // Verify user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (user.getSchool() == null || !user.getSchool().getId().equals(schoolId)) {
            throw new RuntimeException("User does not belong to this school");
        }

        // Get the note
        Note note = noteRepository.findByIdAndSchoolId(noteId, schoolId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Only allow the creator to delete
        if (!note.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the creator can delete this note");
        }

        // Hard delete
        noteRepository.deleteById(noteId);
        log.info("Note hard deleted successfully");
    }
}

