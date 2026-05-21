import { Link, Route, Routes } from "react-router-dom";
import AllNotesPage from "./pages/AllNotesPage";
import CreateNotePage from "./pages/CreateNotePage";
import NoteDetailsPage from "./pages/NoteDetailsPage";

function App() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>Notes Management</h1>
        <nav className="nav-links">
          <Link to="/">All Notes</Link>
          <Link to="/create">Create Note</Link>
        </nav>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={<AllNotesPage />} />
          <Route path="/create" element={<CreateNotePage />} />
          <Route path="/notes/:id" element={<NoteDetailsPage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
