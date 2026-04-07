# html-compose — Connect Four

## Run (Docker)

From the repo root:

```bash
docker compose up dev
```

Open **http://localhost:8080** in your browser.

## Features

- **Configurable board**: rows and columns (1–20), plus **win line length**
- **Two players** (red / yellow), alternating turns
- **Click a column** to play; drop animation
- **Undo last move**
- **New game** opens settings; **play again** after game over
- **Game over** handling: win or draw; per-player **win tally**
- **Browser persistence** via `localStorage` (survives reload)
- **Cursor preview**
- Simple feedback (e.g. full column)
