package de.bsommerfeld.orchestra.persistence.path;

import com.google.inject.Singleton;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides platform-specific paths for storing application data.
 * Supports Windows, macOS, and Linux operating systems.
 */
@Singleton
public class PlatformPathProvider {

    private static final String APP_NAME = "orchestra";
    private static final String DEFAULT_DIRECTORY = "data";
    private static final String SYMPHONIES_DIRECTORY = "symphonies";

    /**
     * Enum representing the supported operating systems.
     */
    public enum OperatingSystem {
        WINDOWS,
        MAC,
        LINUX,
        UNKNOWN
    }

    /**
     * Determines the current operating system.
     *
     * @return The detected operating system
     */
    public OperatingSystem getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac")) {
            return OperatingSystem.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OperatingSystem.LINUX;
        } else {
            return OperatingSystem.UNKNOWN;
        }
    }

    /**
     * Gets the base application data directory for the current platform.
     *
     * @return The path to the application data directory
     */
    public Path getAppDataDirectory() {
        OperatingSystem os = getOperatingSystem();
        String userHome = System.getProperty("user.home");
        
        switch (os) {
            case WINDOWS:
                // On Windows, use %APPDATA%\orchestra
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    return Paths.get(appData, APP_NAME);
                } else {
                    return Paths.get(userHome, "AppData", "Roaming", APP_NAME);
                }
            case MAC:
                // On macOS, use ~/Library/Application Support/orchestra
                return Paths.get(userHome, "Library", "Application Support", APP_NAME);
            case LINUX:
                // On Linux, use ~/.local/share/orchestra
                return Paths.get(userHome, ".local", "share", APP_NAME);
            default:
                // Fallback to a directory in the user's home
                return Paths.get(userHome, "." + APP_NAME);
        }
    }

    /**
     * Gets the directory for storing Symphony data.
     *
     * @return The path to the Symphony data directory
     */
    public Path getSymphonyDirectory() {
        return getAppDataDirectory().resolve(SYMPHONIES_DIRECTORY);
    }

    /**
     * Gets the legacy storage directory path.
     * This is used for backward compatibility with existing data.
     *
     * @return The path to the legacy storage directory
     */
    public Path getLegacyStorageDirectory() {
        return Paths.get(DEFAULT_DIRECTORY, SYMPHONIES_DIRECTORY);
    }
}