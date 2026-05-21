import { Link } from "react-router-dom";

function formatDate(dateString) {
  return new Date(dateString).toLocaleString();
}

function NoteCard({ note }) {
  return (
    <Link to={`/notes/${note.id}`} className="note-card">
      <h3>{note.title}</h3>
      <p className="note-date">Created: {formatDate(note.createdAt)}</p>
    </Link>
  );
}

export default NoteCard;
