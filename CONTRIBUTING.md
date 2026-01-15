# Contributing to UI File Plugin

Thank you for your interest in the UI File Plugin! 🎉

## Setup

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR-USERNAME/hytale-ui-plugin.git
   cd hytale-ui-plugin
   ```
3. Open the project in IntelliJ IDEA
4. Make sure JDK 17+ is installed

## Development

### Testing the Plugin

```bash
./gradlew runIde
```

This starts a test IDE with the plugin installed.

### Running Tests

```bash
./gradlew test
```

### Building the Plugin

```bash
./gradlew buildPlugin
```

The built plugin can be found under `build/distributions/`.

## Pull Requests

1. Create a new branch (`git checkout -b feature/amazing-feature`)
2. Commit your changes (`git commit -m 'Add amazing feature'`)
3. Push to the branch (`git push origin feature/amazing-feature`)
4. Open a Pull Request

## Code Style

- Use 4 spaces for indentation
- Follow Java naming conventions
- Comment complex code

## Questions?

Feel free to open an issue in the repository!

