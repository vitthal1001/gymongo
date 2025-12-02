# GymOnGo

This repository contains a Spring Boot backend for GymOnGo (Java 17, Spring Boot 3, Gradle).

Quick notes:

- Build and test with the included Gradle wrapper:

```bash
# run tests
./gradlew test

# build the jar
./gradlew bootJar
```

- Run locally with Docker Compose (uses Postgres):

```bash
# copy .env.example -> .env and set values (JWT_SECRET, DB credentials)
docker compose up --build
```

- Tests
  - Unit and integration tests run with `./gradlew test` (the Docker build in CI also runs tests).
  - There are new unit tests including a concurrency test for `BookingService` and controller unit tests in `src/test/java`.

- CI / Image publishing
  - The GitHub Actions workflow (`.github/workflows/ci.yml`) runs tests and builds an image.
  - When commits land on `main`/`master`, the workflow will publish the Docker image to GitHub Container Registry (ghcr.io). No extra configuration is required for GHCR beyond the default `GITHUB_TOKEN`.
  - If you'd rather publish to Docker Hub, modify the workflow to use Docker Hub credentials (DOCKERHUB_USERNAME / DOCKERHUB_TOKEN) as repository secrets.

- Pushing to a remote
  - I can push the local git repo to a remote if you provide the remote URL and credentials. Example commands you can run locally:

```bash
# add remote (replace URL)
git remote add origin git@github.com:youruser/gymongo.git
git push -u origin main
```

