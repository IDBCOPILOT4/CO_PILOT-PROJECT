import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { deleteNote, fetchNoteById, updateNote } from "../services/noteApi";

function formatDate(dateString) {
  return new Date(dateString).toLocaleString();
}

function NoteDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [note, setNote] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [draftTitle, setDraftTitle] = useState("");
  const [draftContent, setDraftContent] = useState("");
  const [originalTitle, setOriginalTitle] = useState("");
  const [originalContent, setOriginalContent] = useState("");

  useEffect(() => {
    async function loadNote() {
      try {
        const data = await fetchNoteById(id);
        setNote(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    loadNote();
  }, [id]);

  function handleStartEdit() {
    if (!note) {
      return;
    }

    setDraftTitle(note.title);
    setDraftContent(note.content);
    setOriginalTitle(note.title);
    setOriginalContent(note.content);
    setError("");
    setIsEditMode(true);
  }

  async function handleSave() {
    if (isSaving) {
      return;
    }

    if (!draftTitle.trim() || !draftContent.trim()) {
      setError("Title and content are required");
      return;
    }

    setIsSaving(true);
    try {
      const updatedNote = await updateNote(id, {
        title: draftTitle,
        content: draftContent
      });
      setNote(updatedNote);
      setIsEditMode(false);
      setError("");
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSaving(false);
    }
  }

  function handleCancel() {
    const hasChanges = draftTitle !== originalTitle || draftContent !== originalContent;

    if (hasChanges) {
      const shouldDiscard = window.confirm("Are you sure you want to discard your changes?");
      if (!shouldDiscard) {
        return;
      }
    }

    setDraftTitle(originalTitle);
    setDraftContent(originalContent);
    setIsEditMode(false);
    setError("");
  }

  async function handleDeleteNote() {
    const confirmed = window.confirm("Are you sure you want to delete this note?");

    if (!confirmed) {
      return;
    }

    try {
      setDeleteLoading(true);
      setError("");
      await deleteNote(id);
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setDeleteLoading(false);
    }
  }

  if (loading) {
    return <p>Loading note...</p>;
  }

  if (error) {
    return <p className="error-text">{error}</p>;
  }

  if (!note) {
    return <p>Note not found.</p>;
  }

  return (
    <section className="note-details">
      {isEditMode ? (
        <input
          type="text"
          value={draftTitle}
          onChange={(event) => setDraftTitle(event.target.value)}
          placeholder="Enter note title"
        />
      ) : (
        <h2>{note.title}</h2>
      )}
      <p className="note-date">Created: {formatDate(note.createdAt)}</p>
      {isEditMode ? (
        <textarea
          rows="8"
          value={draftContent}
          onChange={(event) => setDraftContent(event.target.value)}
          placeholder="Write your note..."
        />
      ) : (
        <p className="note-content">{note.content}</p>
      )}

      <div className="button-row">
        {isEditMode ? (
          <>
            <button type="button" onClick={handleSave} disabled={isSaving}>
              {isSaving ? "Saving..." : "Save"}
            </button>
            <button type="button" onClick={handleCancel} disabled={isSaving}>
              Cancel
            </button>
          </>
        ) : (
          <>
            <button type="button" onClick={handleStartEdit}>
              Edit
            </button>
            <button type="button" onClick={handleDeleteNote} className="danger" disabled={deleteLoading}>
              {deleteLoading ? "Deleting..." : "Delete"}
            </button>
            <button type="button" onClick={() => navigate("/")}>
              Return
            </button>
          </>
        )}
      </div>
    </section>
  );
}

export default NoteDetailsPage;
