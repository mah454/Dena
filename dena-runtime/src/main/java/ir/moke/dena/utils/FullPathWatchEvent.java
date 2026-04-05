package ir.moke.dena.utils;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Objects;

public record FullPathWatchEvent(WatchEvent.Kind<?> kind,
                                 String fileName,
                                 Path fullPath,
                                 int count) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FullPathWatchEvent that = (FullPathWatchEvent) o;
        return count == that.count && Objects.equals(fullPath, that.fullPath) && Objects.equals(fileName, that.fileName) && Objects.equals(kind, that.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, fileName, fullPath, count);
    }
}
