import { useEffect, useState } from "react";
import NoteCard from "../components/NoteCard";
import { fetchAllNotes } from "../services/noteApi";

function AllNotesPage() {
  const [notes, setNotes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadNotes() {
      try {
        const data = await fetchAllNotes();
        setNotes(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }

    loadNotes();
  }, []);

  if (loading) {
    return <p>Loading notes...</p>;
  }

  if (error) {
    return <p className="error-text">{error}</p>;
  }

  if (notes.length === 0) {
    return <p>No notes yet. Create your first note.</p>;
  }

  return (
    <section>
      <h2>All Notes</h2>
      <div className="note-grid">
        {notes.map((note) => (
          <NoteCard key={note.id} note={note} />
        ))}
      </div>
    </section>
  );
}

export default AllNotesPage;
