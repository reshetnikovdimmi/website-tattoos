package ru.tattoo.maxsim.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.tattoo.maxsim.model.Images;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
class ImagesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ImagesRepository repository;

    private Images image(String name, String category, String userName, boolean flag) {
        Images image = new Images();
        image.setImageName(name);
        image.setCategory(category);
        image.setUserName(userName);
        image.setFlag(flag);
        image.setDescription(name + " description");
        return image;
    }

    @Test
    void findsByCategoryAndUserName() {
        entityManager.persistAndFlush(image("one.jpg", "Реализм", "alice", false));
        entityManager.persistAndFlush(image("two.jpg", "Реализм", "bob", false));
        entityManager.persistAndFlush(image("three.jpg", "Олдскул", "alice", false));

        Page<Images> page = repository.findByCategoryAndUserName(
                "Реализм", "alice", PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(Images::getImageName)
                .containsExactly("one.jpg");
    }

    @Test
    void findsFeaturedImagesAsList() {
        entityManager.persistAndFlush(image("one.jpg", "Реализм", "alice", true));
        entityManager.persistAndFlush(image("two.jpg", "Реализм", "alice", false));

        List<Images> featured = repository.findByFlagTrue();

        assertThat(featured)
                .extracting(Images::getImageName)
                .containsExactly("one.jpg");
    }
}
