# JobEngine

A small live job-search web app for Render.

## Deploy

Upload the contents of this folder to the root of a GitHub repository, then create a Render Blueprint from that repository. Render will use `render.yaml` and the `Dockerfile` automatically.

The app serves the web UI at `/`, health checks at `/health`, and jobs at `/jobs?experience=1`.
