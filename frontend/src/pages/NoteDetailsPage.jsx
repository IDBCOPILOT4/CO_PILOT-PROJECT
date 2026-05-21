import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { fetchNoteById } from "../services/noteApi";

function formatDate(dateString) {
  return new Date(dateString).toLocaleString();
}

function NoteDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [note, setNote] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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

  function handleDeletePlaceholder() {
    // Placeholder only: delete logic not implemented yet.
    alert("Delete feature will be added later.");
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
        <button type="button" onClick={handleDeletePlaceholder} className="danger">
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
