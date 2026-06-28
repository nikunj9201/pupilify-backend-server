package com.smartschool.api.service;

import com.smartschool.api.entity.Note;
import java.util.List;
import java.util.Optional;

public interface NoteService {

    // Create a new note
    Note createNote(Note note, Long schoolId, String username);

    // Get all notes for a school
    List<Note> getNotesBySchool(Long schoolId, String username);

    // Get a specific note
    Optional<Note> getNoteById(Long noteId, Long schoolId, String username);

    // Update a note
    Note updateNote(Long noteId, Note noteDetails, Long schoolId, String username);

    // Delete a note (soft delete)
    void deleteNote(Long noteId, Long schoolId, String username);

    // Hard delete a note
    void hardDeleteNote(Long noteId, Long schoolId, String username);
}

