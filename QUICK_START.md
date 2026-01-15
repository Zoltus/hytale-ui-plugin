# Quick Start Guide

## For Developers: Build and Install the Plugin

### Step 1: Clone the Repository

```bash
git clone https://github.com/BungeeDEV/hytale-ui-plugin.git
cd hytale-ui-plugin
```

### Step 2: Build the Plugin

**Windows:**
```bash
gradlew.bat buildPlugin
```

**Linux/Mac:**
```bash
./gradlew buildPlugin
```

⏱️ The first build takes a bit longer (approx. 1-2 minutes) as Gradle downloads dependencies.

### Step 3: Install the Plugin

1. The built plugin is located at: `build/distributions/ui-file-plugin-1.0.0.zip`
2. Open IntelliJ IDEA
3. Go to: `File` → `Settings` → `Plugins`
4. Click the gear icon ⚙️ → `Install Plugin from Disk...`
5. Select the ZIP file
6. Restart IntelliJ

### Step 4: Test the Plugin

1. Create a new `.ui` file in your project
2. Open `example.ui` from the repository as a template
3. Enjoy the syntax highlighting! 🎨

---

## For Plugin Developers: Direct Testing

Instead of installing the plugin, you can test it directly:

```bash
gradlew.bat runIde
```

This starts a test IDE with the plugin already loaded.

---

## Common Issues

### "Java version not found"

Make sure JDK 17+ is installed:
```bash
java -version
```

If the wrong version is being used, set in `gradle.properties`:
```properties
org.gradle.java.home=/path/to/your/jdk-17
```

### Build fails

Try a clean build first:
```bash
gradlew.bat clean buildPlugin
```

---

## Next Steps

- Read the full [README.md](README.md)
- Check out [CONTRIBUTING.md](CONTRIBUTING.md) if you want to contribute
- Open an issue for problems or feature requests

