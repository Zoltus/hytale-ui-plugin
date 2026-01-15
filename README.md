# UI File Plugin for IntelliJ IDEA

An IntelliJ IDEA plugin that provides support for `.ui` files - a format for defining user interfaces.

## Features

✅ **Syntax Highlighting** - Color highlighting for components, properties, strings, and comments  
✅ **Bracket Matching** - Automatic matching of `{}` and `()`  

## Requirements

- **IntelliJ IDEA 2025.1** or newer
- **JDK 17 or higher** (for development)

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/BungeeDEV/hytale-ui-plugin.git
cd hytale-ui-plugin
```

### 2. Build the Plugin
```bash
./gradlew buildPlugin
```

### 3. Install the Plugin

- Find the built plugin at: `build/distributions/ui-file-plugin-1.0.0.zip`
- In IntelliJ: `File → Settings → Plugins → ⚙️ → Install Plugin from Disk...`
- Select the `.jar` file and restart the IDE

### Alternative: Test the Plugin Directly

```bash
./gradlew runIde
```

This starts a test IDE with the plugin already installed.

## UI File Format Example

```ui
// Comment
Group {
  Anchor: (Width: 400, Height: 250);
  Background: #1a1a2e;
  LayoutMode: Top;
  Padding: (Full: 20);

  Label #Title {
    Text: "Hello World!";
    Anchor: (Height: 40);
    Style: (FontSize: 24, TextColor: #ffffff, Alignment: Center);
  }
  
  Button #SubmitButton {
    Text: "Click Me";
    Anchor: (Width: 150, Height: 35);
    Background: #0066cc;
  }
}
```

## Development

### Useful Gradle Tasks

```bash
./gradlew buildPlugin    # Build the plugin
./gradlew runIde         # Start test IDE
./gradlew test           # Run tests
./gradlew verifyPlugin   # Verify plugin
```

### Project Structure

```
ui-plugin/
├── src/main/java/de/bungee/uifile/
│   ├── UILanguage.java
│   ├── UIFileType.java
│   ├── lexer/          # Tokenizer
│   ├── parser/         # Parser
│   ├── psi/            # PSI structures
│   └── highlighter/    # Syntax highlighting
└── src/main/resources/
    └── META-INF/
        └── plugin.xml
```

## License

MIT License

## Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

