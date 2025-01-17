package org.spongepowered.asm.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class for file operations
 */
public final class Files {

    private Files() {
    }

    /**
     * Convert a URL to a file but handle a corner case with UNC paths which do
     * not parse correctly.
     *
     * @param url URL to convert
     * @return File
     * @throws URISyntaxException if the URL cannot be converted to a URI
     */
    public static File toFile(URL url) throws URISyntaxException {
        return url != null ? Files.toFile(url.toURI()) : null;
    }

    /**
     * Convert a URI to a file but handle a corner case with UNC paths which do
     * not parse correctly.
     *
     * @param uri URI to convert
     * @return File
     */
    public static File toFile(URI uri) {
        if (uri == null) {
            return null;
        }

        if ("file".equals(uri.getScheme()) && uri.getAuthority() != null) {
            String strUri = uri.toString();
            if (strUri.startsWith("file://") && !strUri.startsWith("file:///")) {
                try {
                    uri = new URI("file:////" + strUri.substring(7));
                } catch (URISyntaxException ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            }
        }

        return new File(uri);
    }

    /**
     * Recursively delete a directory. Does not support symlinks but this is
     * mainly used by mixin internals which only write files and normal
     * directories so it should be fine
     *
     * @param dir Root directory to delete
     * @throws IOException Security errors and any other IO errors encountered
     *      are raised as IOExceptions
     */
    public static void deleteRecursively(File dir) throws IOException {
        if (dir == null || !dir.isDirectory()) {
            return;
        }

        try {
            File[] files = dir.listFiles();
            if (files == null) {
                throw new IOException("Error enumerating directory during recursive delete operation: " + dir.getAbsolutePath());
            }

            for (File child : files) {
                if (child.isDirectory()) {
                    Files.deleteRecursively(child);
                } else if (child.isFile()) {
                    if (!child.delete()) {
                        throw new IOException("Error deleting file during recursive delete operation: " + child.getAbsolutePath());
                    }
                }
            }

            if (!dir.delete()) {
                throw new IOException("Error deleting directory during recursive delete operation: " + dir.getAbsolutePath());
            }
        } catch (SecurityException ex) {
            throw new IOException("Security error during recursive delete operation", ex);
        }
    }

}
