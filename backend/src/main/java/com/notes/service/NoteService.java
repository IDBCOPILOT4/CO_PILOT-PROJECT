package com.notes.service;

import com.notes.model.Note;
import com.notes.repository.NoteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service layer for note business operations.
 *
 * Centralizes not-found handling so controllers can map errors consistently.
 */
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Retrieves all notes.
     */
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    /**
     * Retrieves one note or throws if it does not exist.
     *
     * @throws IllegalArgumentException when no note exists for the provided id
     */
    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));
    }

    /**
     * Creates and persists a new note.
     */
    public Note createNote(String title, String content) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        return noteRepository.save(note);
    }

    /**
     * Deletes an existing note.
     *
     * @throws IllegalArgumentException when no note exists for the provided id
     */
    public void deleteNote(Long id) {
        noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));
        noteRepository.deleteById(id);
    }

    /**
     * Updates title and content for an existing note.
     *
     * @throws IllegalArgumentException when no note exists for the provided id
     */
    public Note updateNote(Long id, String title, String content) {
        Note note = getNoteById(id);
        note.setTitle(title);
        note.setContent(content);
        return noteRepository.save(note);
    }
}
