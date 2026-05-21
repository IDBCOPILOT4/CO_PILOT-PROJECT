import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createNote } from "../services/noteApi";

function CreateNotePage() {
  const navigate = useNavigate();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSaving(true);

    try {
      await createNote({ title, content });
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <section>
      <h2>Create Note</h2>
      <form className="note-form" onSubmit={handleSubmit}>
        <label htmlFor="title">Title</label>
        <input
          id="title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />

        <label htmlFor="content">Content</label>
        <textarea
          id="content"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          rows={8}
          required
        />

        {error && <p className="error-text">{error}</p>}

        <button type="submit" disabled={saving}>
          {saving ? "Saving..." : "Save Note"}
        </button>
      </form>
    </section>
  );
}

export default CreateNotePage;
