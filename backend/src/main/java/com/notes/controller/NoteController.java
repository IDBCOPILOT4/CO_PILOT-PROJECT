package com.notes.controller;

import com.notes.model.Note;
import com.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for note CRUD operations.
 *
 * Exposes endpoints used by the React frontend and returns validation/not-found
 * messages as plain text to keep client-side error handling simple.
 */
@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "Operations for managing notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "Get all notes", description = "Returns all notes ordered by repository default ordering")
    @ApiResponse(responseCode = "200", description = "Notes retrieved successfully")
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by id", description = "Returns a single note by identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)
    })
    public Note getNoteById(@PathVariable Long id) {
        return noteService.getNoteById(id);
    }

    @PostMapping
    @Operation(summary = "Create note", description = "Creates a note with title and content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Note created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<Note> createNote(@Valid @RequestBody CreateNoteRequest request) {
        Note createdNote = noteService.createNote(request.getTitle(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note", description = "Deletes a note by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Note deleted"),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)
    })
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note", description = "Updates title and content for an existing note")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404", description = "Note not found", content = @Content)
    })
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @Valid @RequestBody CreateNoteRequest request) {
        Note updatedNote = noteService.updateNote(id, request.getTitle(), request.getContent());
        return ResponseEntity.ok(updatedNote);
    }

    @Operation(hidden = true)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @Operation(hidden = true)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Invalid request";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @Schema(name = "CreateNoteRequest", description = "Payload used when creating or updating a note")
    public static class CreateNoteRequest {
        @Schema(description = "Note title", example = "Project checklist")
        @NotBlank(message = "Title is required")
        private String title;

        @Schema(description = "Note body content", example = "Finalize API docs and run tests")
        @NotBlank(message = "Content is required")
        private String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
