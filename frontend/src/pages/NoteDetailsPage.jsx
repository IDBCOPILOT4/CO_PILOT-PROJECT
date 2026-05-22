import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { deleteNote, fetchNoteById } from "../services/noteApi";

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

  function handleEditPlaceholder() {
    // Placeholder only: edit logic not implemented yet.
    alert("Edit feature will be added later.");
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
      <h2>{note.title}</h2>
      <p className="note-date">Created: {formatDate(note.createdAt)}</p>
      <p className="note-content">{note.content}</p>

      <div className="button-row">
        <button type="button" onClick={handleEditPlaceholder}>
          Edit
        </button>
        <button type="button" onClick={handleDeleteNote} className="danger" disabled={deleteLoading}>
          Delete
        </button>
        <button type="button" onClick={() => navigate("/")}>
          Return
        </button>
      </div>
    </section>
  );
}

export default NoteDetailsPage;
