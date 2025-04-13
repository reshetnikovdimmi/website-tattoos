package ru.tattoo.maxsim.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tattoo.maxsim.model.DTO.GalleryDto;
import ru.tattoo.maxsim.service.interf.ImagesService;
import ru.tattoo.maxsim.util.PageSize;



/**
 * Класс для интеграционного тестирования контроллера GalleryController.
 * Этот класс проверяет взаимодействие контроллера с остальными частями приложения,
 * такими как службы и представления.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GalleryControllerIT {

    private static final int PAGE_NUMBER = 0;
    /**
     * MockMvc — это инструмент для тестирования веб-МVC слоев в Spring.
     * Он позволяет отправлять HTTP-запросы и проверять полученные ответы.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * MockBean — это мока службы ImagesService.
     * Вместо реального вызова службы, мы заменяем её мокой, чтобы избежать обращения к реальной базе данных.
     */
    @MockBean
    private ImagesService imagesService;

    /**
     * Тестирует метод GET для маршрута "/gallery" без параметров.
     * Проверяет, что метод возвращает правильный вид и передает нужные данные в модель.
     *
     * @throws Exception Возникает, если возникают проблемы при выполнении запроса.
     */
    @Test
    public void shouldReturnGalleryViewWhenCalledWithoutParameters() throws Exception {
        // Arrange
        // Настраиваем моку ImagesService, чтобы вернуть фиктивный объект GalleryDto при вызове метода getGalleryDto.
        when(imagesService.getGalleryDto(null, null, PageSize.IMG_9.getPageSize(), PAGE_NUMBER))
                .thenReturn(new GalleryDto());

        // Act
        // Посылаем GET-запрос на маршрут "/gallery" и сохраняем результат.
        mockMvc.perform(get("/gallery"))

                // Assert
                // Проверяем, что ответ успешен (статус 200 OK), возвращаемое представление — "gallery",
                // и в модели присутствует атрибут "gallery".
                .andExpect(status().isOk())
                .andExpect(view().name("gallery"))
                .andExpect(model().attributeExists("gallery"));
    }

    /**
     * Тестирует метод GET для маршрута "/gallery/{style}/{page}/{number}",
     * когда передается стиль, страница и количество изображений.
     * Проверяет, что метод возвращает правильный вид и передает нужные данные в модель.
     *
     * @throws Exception Возникает, если возникают проблемы при выполнении запроса.
     */
    @Test
    public void shouldReturnFilteredGalleryViewWhenStyleIsProvided() throws Exception {
        // Arrange
        // Определяем тестовые значения для стиля, страницы и количества изображений.
        String style = "Landscape";
        int page = 1;
        int number = 9;

        // Настраиваем моку ImagesService, чтобы вернуть фиктивный объект GalleryDto при вызове метода getGalleryDto.
        when(imagesService.getGalleryDto(style, null, number, page))
                .thenReturn(new GalleryDto());

        // Act
        // Посылаем GET-запрос на маршрут "/gallery/Landscape/1/9" и сохраняем результат.
        mockMvc.perform(get("/gallery/{style}/{page}/{number}", style, page, number))

                // Assert
                // Проверяем, что ответ успешен (статус 200 OK), возвращаемое представление — "gallery::galleryFilter",
                // и в модели присутствует атрибут "gallery".
                .andExpect(status().isOk())
                .andExpect(view().name("gallery::galleryFilter"))
                .andExpect(model().attributeExists("gallery"));
    }

    /**
     * Тестирует метод GET для маршрута "/gallery/reviews/{page}/{number}",
     * когда передаются страница и количество изображений.
     * Проверяет, что метод возвращает правильный вид и передает нужные данные в модель.
     *
     * @throws Exception Возникает, если возникают проблемы при выполнении запроса.
     */
    @Test
    public void shouldReturnReviewsModalWhenPageAndNumberAreProvided() throws Exception {
        // Arrange
        // Определяем тестовые значения для страницы и количества изображений.
        int page = 1;
        int number = 15;

        // Настраиваем моку ImagesService, чтобы вернуть фиктивный объект GalleryDto при вызове метода getGalleryDto.
        when(imagesService.getGalleryDto(null, null, number, page))
                .thenReturn(new GalleryDto());

        // Act
        // Посылаем GET-запрос на маршрут "/gallery/reviews/1/15" и сохраняем результат.
        mockMvc.perform(get("/gallery/reviews/{page}/{number}", page, number))

                // Assert
                // Проверяем, что ответ успешен (статус 200 OK), возвращаемое представление — "fragments::modal-img",
                // и в модели присутствует атрибут "gallery".
                .andExpect(status().isOk())
                .andExpect(view().name("fragments::modal-img"))
                .andExpect(model().attributeExists("gallery"));
    }
}
