package ru.tattoo.maxsim.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Собирает имена изображений, на которые ссылается БД.
 *
 * <p>Используется очисткой сирот: файл, которого нет ни в одной сущности
 * с полем {@code imageName}, считается кандидатом на удаление.
 */
@Service
@Slf4j
public class ImageReferenceService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Set<String> collectReferencedImageNames() {
        Set<String> referenced = new HashSet<>();

        for (EntityType<?> type : entityManager.getMetamodel().getEntities()) {
            boolean hasImageName = type.getAttributes().stream()
                    .anyMatch(attribute -> "imageName".equals(attribute.getName()));
            if (!hasImageName) {
                continue;
            }

            String entityName = type.getName();
            try {
                List<String> names = entityManager.createQuery(
                                "select e.imageName from " + entityName
                                        + " e where e.imageName is not null and e.imageName <> ''",
                                String.class)
                        .getResultList();
                referenced.addAll(names);
            } catch (RuntimeException e) {
                // Не удаляем файлы, если не смогли прочитать хотя бы одну таблицу:
                // ошибка чтения не должна приводить к массовому удалению.
                log.error("Не удалось собрать imageName из сущности {}: {}", entityName, e.getMessage(), e);
                throw new IllegalStateException("Не удалось собрать ссылки на изображения", e);
            }
        }

        log.debug("Найдено {} уникальных ссылок на изображения", referenced.size());
        return Set.copyOf(referenced);
    }
}
