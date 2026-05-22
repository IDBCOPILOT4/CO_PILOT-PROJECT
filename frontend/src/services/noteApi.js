const API_BASE = "/api/notes";

/**
 * Fetches all notes from the backend API.
 */
export async function fetchAllNotes() {
  const response = await fetch(API_BASE);
  if (!response.ok) {
    throw new Error("Failed to fetch notes");
  }
  return response.json();
}

/**
 * Fetches one note by id and normalizes not-found errors.
 */
export async function fetchNoteById(id) {
  const response = await fetch(`${API_BASE}/${id}`);
  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("Note not found");
    }
    throw new Error("Failed to fetch note");
  }
  return response.json();
}

/**
 * Creates a new note.
 */
export async function createNote(note) {
  const response = await fetch(API_BASE, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(note)
  });

  if (!response.ok) {
    throw new Error("Failed to create note");
  }

  return response.json();
}

/**
 * Deletes one note by id.
 */
export async function deleteNote(id) {
  const response = await fetch(`${API_BASE}/${id}`, {
    method: "DELETE"
  });

  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("Note not found");
    }
    throw new Error("Failed to delete note");
  }
}

/**
 * Updates a note and surfaces backend validation text when available.
 */
export async function updateNote(id, note) {
  const response = await fetch(`${API_BASE}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(note)
  });

  if (!response.ok) {
    const errorMessage = (await response.text()) || "Failed to update note";
    throw new Error(errorMessage);
  }
  return response.json();
}
