Frog_ex
A 2D Java-based game built using the LibGDX framework. This project was developed as a personal hobby project and was selected to be showcased at [Insert Name of Exhibition].

🎮 About the Game
[Write 1 sentence here: e.g., A side-scrolling platformer where the player controls a frog navigating through traffic and rivers.]

🚀 Key Features
Engine: Built from scratch using Java and LibGDX.

Mechanics: [Mention one cool thing, e.g., Custom collision detection or physics].

Exhibition: Successfully presented to public users at [Exhibition Name].

🛠️ Tech Stack
Java (Core Logic)

LibGDX (Game Engine)

Gradle (Build Tool)
## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
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
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl2:jar`: builds application's runnable jar, which can be found at `lwjgl2/build/libs`.
- `lwjgl2:run`: starts the application.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
