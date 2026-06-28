package com.smartschool.api.controller;

import com.smartschool.api.entity.Note;
import com.smartschool.api.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/notes")
@CrossOrigin("*")
public class NoteController {

    private static final Logger log = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService noteService;

    /**
     * Get the username from the JWT token
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }

    /**
     * Create a new note
     * POST /api/admin/notes/school/{schoolId}
     */
    @PostMapping("/school/{schoolId}")
    public ResponseEntity<?> createNote(@PathVariable Long schoolId, @RequestBody Note note) {
        try {
            log.info("Creating note for school: {}", schoolId);
            String username = getCurrentUsername();
            Note createdNote = noteService.createNote(note, schoolId, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (RuntimeException e) {
            log.error("Error creating note: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Get all notes for a school
     * GET /api/admin/notes/school/{schoolId}
     */
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<?> getNotesBySchool(@PathVariable Long schoolId) {
        try {
            log.info("Fetching notes for school: {}", schoolId);
            String username = getCurrentUsername();
            List<Note> notes = noteService.getNotesBySchool(schoolId, username);
            return ResponseEntity.ok(notes);
        } catch (RuntimeException e) {
            log.error("Error fetching notes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Get a specific note by ID
     * GET /api/admin/notes/school/{schoolId}/note/{noteId}
     */
    @GetMapping("/school/{schoolId}/note/{noteId}")
    public ResponseEntity<?> getNoteById(@PathVariable Long schoolId, @PathVariable Long noteId) {
        try {
            log.info("Fetching note: {} for school: {}", noteId, schoolId);
            String username = getCurrentUsername();
            Optional<Note> note = noteService.getNoteById(noteId, schoolId, username);

            if (note.isPresent()) {
                return ResponseEntity.ok(note.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
            }
        } catch (RuntimeException e) {
            log.error("Error fetching note: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Update a note
     * PUT /api/admin/notes/school/{schoolId}/note/{noteId}
     */
    @PutMapping("/school/{schoolId}/note/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable Long schoolId, @PathVariable Long noteId, @RequestBody Note noteDetails) {
        try {
            log.info("Updating note: {} for school: {}", noteId, schoolId);
            String username = getCurrentUsername();
            Note updatedNote = noteService.updateNote(noteId, noteDetails, schoolId, username);
            return ResponseEntity.ok(updatedNote);
        } catch (RuntimeException e) {
            log.error("Error updating note: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Delete a note (soft delete)
     * DELETE /api/admin/notes/school/{schoolId}/note/{noteId}
     */
    @DeleteMapping("/school/{schoolId}/note/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable Long schoolId, @PathVariable Long noteId) {
        try {
            log.info("Deleting note: {} for school: {}", noteId, schoolId);
            String username = getCurrentUsername();
            noteService.deleteNote(noteId, schoolId, username);
            return ResponseEntity.ok("Note deleted successfully");
        } catch (RuntimeException e) {
            log.error("Error deleting note: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * Hard delete a note (permanent delete)
     * DELETE /api/admin/notes/school/{schoolId}/note/{noteId}/hard
     */
    @DeleteMapping("/school/{schoolId}/note/{noteId}/hard")
    public ResponseEntity<?> hardDeleteNote(@PathVariable Long schoolId, @PathVariable Long noteId) {
        try {
            log.info("Hard deleting note: {} for school: {}", noteId, schoolId);
            String username = getCurrentUsername();
            noteService.hardDeleteNote(noteId, schoolId, username);
            return ResponseEntity.ok("Note permanently deleted");
        } catch (RuntimeException e) {
            log.error("Error hard deleting note: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}

