package com.notes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.notes.model.Note;
import com.notes.repository.NoteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void getAllNotes_returnsAllNotes() {
        Note note1 = buildNote(1L, "Title 1", "Content 1");
        Note note2 = buildNote(2L, "Title 2", "Content 2");
        when(noteRepository.findAll()).thenReturn(List.of(note1, note2));

        List<Note> result = noteService.getAllNotes();

        assertEquals(2, result.size());
        assertEquals("Title 1", result.get(0).getTitle());
        assertEquals("Title 2", result.get(1).getTitle());
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    void getAllNotes_whenEmpty_returnsEmptyList() {
        when(noteRepository.findAll()).thenReturn(List.of());

        List<Note> result = noteService.getAllNotes();

        assertTrue(result.isEmpty());
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    void getNoteById_whenExists_returnsNote() {
        Note note = buildNote(10L, "Hello", "World");
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        Note result = noteService.getNoteById(10L);

        assertEquals(10L, result.getId());
        assertEquals("Hello", result.getTitle());
        assertEquals("World", result.getContent());
        verify(noteRepository, times(1)).findById(10L);
    }

    @Test
    void getNoteById_whenMissing_throwsIllegalArgumentException() {
        when(noteRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> noteService.getNoteById(99L));

        assertEquals("Note not found with id: 99", ex.getMessage());
        verify(noteRepository, times(1)).findById(99L);
    }

    @Test
    void createNote_savesAndReturnsCreatedNote() {
        when(noteRepository.save(org.mockito.ArgumentMatchers.any(Note.class))).thenAnswer(invocation -> {
            Note toSave = invocation.getArgument(0);
            toSave.setId(7L);
            return toSave;
        });

        Note result = noteService.createNote("New title", "New content");

        assertEquals(7L, result.getId());
        assertEquals("New title", result.getTitle());
        assertEquals("New content", result.getContent());

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository, times(1)).save(noteCaptor.capture());
        assertEquals("New title", noteCaptor.getValue().getTitle());
        assertEquals("New content", noteCaptor.getValue().getContent());
    }

    @Test
    void updateNote_whenExists_updatesAndReturnsNote() {
        Note existing = buildNote(2L, "Old", "Old content");
        when(noteRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(noteRepository.save(existing)).thenReturn(existing);

        Note result = noteService.updateNote(2L, "Updated", "Updated content");

        assertEquals(2L, result.getId());
        assertEquals("Updated", result.getTitle());
        assertEquals("Updated content", result.getContent());
        verify(noteRepository, times(1)).findById(2L);
        verify(noteRepository, times(1)).save(existing);
    }

    @Test
    void updateNote_whenMissing_throwsIllegalArgumentException() {
        when(noteRepository.findById(123L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> noteService.updateNote(123L, "T", "C"));

        assertEquals("Note not found with id: 123", ex.getMessage());
        verify(noteRepository, times(1)).findById(123L);
        verify(noteRepository, never()).save(org.mockito.ArgumentMatchers.any(Note.class));
    }

    @Test
    void deleteNote_whenExists_deletesById() {
        Note existing = buildNote(4L, "A", "B");
        when(noteRepository.findById(4L)).thenReturn(Optional.of(existing));

        noteService.deleteNote(4L);

        verify(noteRepository, times(1)).findById(4L);
        verify(noteRepository, times(1)).deleteById(4L);
    }

    @Test
    void deleteNote_whenMissing_throwsIllegalArgumentException() {
        when(noteRepository.findById(200L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> noteService.deleteNote(200L));

        assertEquals("Note not found with id: 200", ex.getMessage());
        verify(noteRepository, times(1)).findById(200L);
        verify(noteRepository, never()).deleteById(200L);
    }

    private Note buildNote(Long id, String title, String content) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        return note;
    }
}