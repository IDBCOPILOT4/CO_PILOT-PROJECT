# Notes Management App

Full-stack Notes Management app with:
- Backend: Java Spring Boot
- Frontend: React + Vite

Current scope:
- Create note
- Read all notes
- Read single note
- Update note
- Delete note

## Architecture Overview

This project uses a simple three-layer backend architecture and a page-driven React frontend.

- Frontend pages call the API layer in `frontend/src/services/noteApi.js`.
- API requests go to Spring controllers under `/api/notes`.
- Controllers delegate business logic to `NoteService`.
- `NoteService` reads and writes data via `NoteRepository`.
- Data is stored in an in-memory H2 database for local development.

Request flow:
Frontend -> NoteController -> NoteService -> NoteRepository -> H2 database

## Project Structure

- backend: Spring Boot API
- frontend: React app

## Run Backend

1. Open terminal in backend folder
2. Run:

mvn spring-boot:run

Backend runs at http://localhost:8080

Swagger API docs are available at:

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/v3/api-docs

## Run Frontend

1. Open terminal in frontend folder
2. Install dependencies:

npm install

3. Start dev server:

npm run dev

Frontend runs at http://localhost:5173

## API Endpoints

- GET /api/notes
- GET /api/notes/{id}
- POST /api/notes
- PUT /api/notes/{id}
- DELETE /api/notes/{id}

Example POST body:

{
  "title": "My note",
  "content": "Some content"
}

## Development Notes

- Backend validation uses `@NotBlank` for `title` and `content`.
- Not-found responses return `404` with a plain text message.
- `createdAt` is set automatically by the backend when a note is first saved.

## Testing

Backend:

- Run all tests: `mvn test`

Frontend:

- Build check: `npm run build`

## Troubleshooting

- Port conflict on backend (8080): stop the process using the port, then restart `mvn spring-boot:run`.
- Frontend cannot call backend: ensure backend is running before starting frontend.
- Empty data after restart: H2 is in-memory, so data is reset when backend stops.
