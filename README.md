# Notes Management App

Full-stack Notes Management app with:
- Backend: Java Spring Boot
- Frontend: React + Vite

Current scope:
- Create note
- Read all notes
- Read single note
- Edit/Delete buttons are placeholders only (no update/delete logic)

## Project Structure

- backend: Spring Boot API
- frontend: React app

## Run Backend

1. Open terminal in backend folder
2. Run:

mvn spring-boot:run

Backend runs at http://localhost:8080

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

Example POST body:

{
  "title": "My note",
  "content": "Some content"
}

test to push
