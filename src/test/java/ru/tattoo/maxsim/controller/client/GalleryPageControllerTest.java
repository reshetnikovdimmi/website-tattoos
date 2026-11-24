package ru.tattoo.maxsim.controller.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tattoo.maxsim.model.DTO.GalleryQuery;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;
import ru.tattoo.maxsim.service.interf.ClassesSectionService;
import ru.tattoo.maxsim.service.interf.ImagesQueryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class GalleryPageControllerTest {

    private ImagesQueryService imagesService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        imagesService = mock(ImagesQueryService.class);
        ClassesSectionService classes = mock(ClassesSectionService.class);
        when(imagesService.getGalleryPage(org.mockito.ArgumentMatchers.any(), isNull()))
                .thenReturn(new PageResult<>(List.<Images>of(), 0, 9, 0, 0, List.of(9, 12, 15)));

        GalleryController controller = new GalleryController(imagesService, classes);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void filterRequestPassesTrimmedStyleAndReturnsGridFragment() throws Exception {
        mockMvc.perform(get("/gallery")
                        .param("style", "  Реализм  ")
                        .param("fragment", "grid"))
                .andExpect(status().isOk())
                .andExpect(view().name("gallery::galleryFilter"));

        verify(imagesService).getGalleryPage(
                argThat(query -> "Реализм".equals(query.category())),
                isNull());
    }

    @Test
    void allGalleryStyleIsNormalizedToNull() throws Exception {
        mockMvc.perform(get("/gallery")
                        .param("style", "Вся галерея")
                        .param("fragment", "grid"))
                .andExpect(status().isOk());

        verify(imagesService).getGalleryPage(
                argThat(query -> query.category() == null),
                isNull());
    }
}
