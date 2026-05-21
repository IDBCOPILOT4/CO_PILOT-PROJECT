const API_BASE = "/api/notes";

export async function fetchAllNotes() {
  const response = await fetch(API_BASE);
  if (!response.ok) {
    throw new Error("Failed to fetch notes");
  }
  return response.json();
}

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
