package ru.tattoo.maxsim.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Поиск внешних утилит (mysqldump, mysql) с учётом явно заданного пути.
 * В Docker-образе они лежат в /usr/bin, локально могут быть в других местах.
 */
public final class ExecutableUtils {

    private ExecutableUtils() {
    }

    public static boolean isExecutable(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }
        if (candidate.contains("/")) {
            return Files.isExecutable(Paths.get(candidate));
        }
        String path = System.getenv("PATH");
        if (path == null || path.isBlank()) {
            return false;
        }
        for (String dir : path.split(java.io.File.pathSeparator)) {
            if (!dir.isBlank() && Files.isExecutable(Path.of(dir, candidate))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param explicit  путь из конфига; если задан — используется без проверок
     * @param candidates имена/пути для автопоиска по порядку
     * @param notFound  текст ошибки, если ничего не найдено
     */
    public static String resolve(String explicit, List<String> candidates, String notFound) {
        if (explicit != null && !explicit.isBlank()) {
            return explicit.trim();
        }
        for (String candidate : candidates) {
            if (isExecutable(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException(notFound);
    }
}
