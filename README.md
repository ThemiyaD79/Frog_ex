# Frog_ex: Educational Java Platformer

A 2D Java-based game built using the **LibGDX** framework. This project was developed as a personal hobby project and was selected to be showcased at the **ICBT Exito Exhibition**.

### 🎮 About the Game
An educational side-scrolling platformer where the player controls a frog navigating through traffic and obstacles. The game gamifies the experience of learning Java: when the player reaches specific checkpoints (Mystery Boxes), a syntax lesson pops up. To advance, the player must verify their learning by solving a mini-puzzle (such as dragging and dropping code blocks to complete the syntax). As the game progresses, both the movement speed and the complexity of the Java concepts increase.

### 🚀 Key Features
* **Educational Gameplay:** Integrates Java syntax lessons directly into the platforming mechanics.
* **Interactive Assessments:** Includes drag-and-drop puzzles to test player understanding before advancing.
* **Progressive Difficulty:** Game speed and coding complexity scale up as the player completes levels.
* **Dynamic State Management:** Implemented custom collision listeners that pause the active game loop to trigger interactive UI overlays for the coding puzzles, seamlessly switching between gameplay and education modes.

### 🏆 Exhibition
Successfully presented to public users at **Exito 202X** at **ICBT Bambalapitiya**.
🏆 Recognition: Received a Certificate of Participation for showcasing this project at the exhibition.

### 🛠️ Tech Stack
* **Java** (Core Logic)
* **LibGDX** (Game Engine)
* **Gradle** (Build Tool)

---

## Platforms
- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `lwjgl2`: Legacy desktop platform using LWJGL2.

## Gradle
This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `lwjgl3:run`: starts the application.
