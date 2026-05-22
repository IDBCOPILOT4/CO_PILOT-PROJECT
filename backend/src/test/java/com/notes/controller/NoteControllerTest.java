package com.notes.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.model.Note;
import com.notes.service.NoteService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NoteService noteService;

    @Test
    void getAllNotes_returns200AndNoteList() throws Exception {
        Note note1 = buildNote(1L, "First", "First content");
        Note note2 = buildNote(2L, "Second", "Second content");
        when(noteService.getAllNotes()).thenReturn(List.of(note1, note2));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("First"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Second"));
    }

    @Test
    void getNoteById_whenFound_returns200AndNote() throws Exception {
        when(noteService.getNoteById(1L)).thenReturn(buildNote(1L, "My title", "My content"));

        mockMvc.perform(get("/api/notes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("My title"))
                .andExpect(jsonPath("$.content").value("My content"));
    }

    @Test
    void getNoteById_whenMissing_returns404() throws Exception {
        when(noteService.getNoteById(99L)).thenThrow(new IllegalArgumentException("Note not found with id: 99"));

        mockMvc.perform(get("/api/notes/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 99"));
    }

    @Test
    void createNote_whenValid_returns201AndBody() throws Exception {
        Note created = buildNote(10L, "Created", "Created content");
        when(noteService.createNote("Created", "Created content")).thenReturn(created);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Created",
                                "content", "Created content"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("Created"))
                .andExpect(jsonPath("$.content").value("Created content"));
    }

    @Test
    void createNote_whenBlankTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", " ",
                                "content", "Valid content"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Title is required"));
    }

    @Test
    void createNote_whenBlankContent_returns400() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Valid",
                                "content", " "
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Content is required"));
    }

    @Test
    void createNote_whenMissingTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Some content\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Title is required"));
    }

    @Test
    void createNote_whenMissingContent_returns400() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Some title\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Content is required"));
    }

    @Test
    void updateNote_whenValid_returns200AndUpdatedBody() throws Exception {
        Note updated = buildNote(5L, "Updated", "Updated content");
        when(noteService.updateNote(5L, "Updated", "Updated content")).thenReturn(updated);

        mockMvc.perform(put("/api/notes/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Updated",
                                "content", "Updated content"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void updateNote_whenMissingNote_returns404() throws Exception {
        when(noteService.updateNote(404L, "x", "y")).thenThrow(new IllegalArgumentException("Note not found with id: 404"));

        mockMvc.perform(put("/api/notes/404")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "x",
                                "content", "y"
                        ))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 404"));
    }

    @Test
    void updateNote_whenBlankTitle_returns400() throws Exception {
        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", " ",
                                "content", "c"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Title is required"));
    }

    @Test
    void updateNote_whenBlankContent_returns400() throws Exception {
        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "t",
                                "content", " "
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Content is required"));
    }

    @Test
    void deleteNote_whenFound_returns204() throws Exception {
        mockMvc.perform(delete("/api/notes/3"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNote_whenMissing_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new IllegalArgumentException("Note not found with id: 3"))
                .when(noteService).deleteNote(3L);

        mockMvc.perform(delete("/api/notes/3"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: 3"));
    }

    private Note buildNote(Long id, String title, String content) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());
        return note;
    }
}