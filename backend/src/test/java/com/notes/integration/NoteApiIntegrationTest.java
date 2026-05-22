package com.notes.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.repository.NoteRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class NoteApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Test
        void openApiDocs_endpointIsAvailable() throws Exception {
                mockMvc.perform(get("/v3/api-docs"))
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("/api/notes")))
                                .andExpect(content().string(containsString("CreateNoteRequest")));
        }

        @Test
    void fullCrudFlow_createReadUpdateDelete_worksEndToEnd() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Integration title",
                                "content", "Integration content"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Integration title"))
                .andExpect(jsonPath("$.content").value("Integration content"))
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long noteId = createdJson.get("id").asLong();

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(noteId))
                .andExpect(jsonPath("$[0].title").value("Integration title"));

        mockMvc.perform(get("/api/notes/{id}", noteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(noteId))
                .andExpect(jsonPath("$.title").value("Integration title"))
                .andExpect(jsonPath("$.content").value("Integration content"));

        mockMvc.perform(put("/api/notes/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Updated integration title",
                                "content", "Updated integration content"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(noteId))
                .andExpect(jsonPath("$.title").value("Updated integration title"))
                .andExpect(jsonPath("$.content").value("Updated integration content"));

        mockMvc.perform(delete("/api/notes/{id}", noteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notes/{id}", noteId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Note not found with id: " + noteId));
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
                                "title", "Valid title",
                                "content", " "
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Content is required"));
    }

    @Test
    void updateNote_whenBlankTitle_returns400() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Before update",
                                "content", "Before update content"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        long noteId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/notes/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", " ",
                                "content", "still valid"
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Title is required"));
    }

    @Test
    void updateNote_whenBlankContent_returns400() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Before update",
                                "content", "Before update content"
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        long noteId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/notes/{id}", noteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "still valid",
                                "content", " "
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Content is required"));
    }
}