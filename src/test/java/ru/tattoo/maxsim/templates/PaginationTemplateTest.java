package ru.tattoo.maxsim.templates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.thymeleaf.TemplateEngine;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.tattoo.maxsim.model.DTO.PageResult;
import ru.tattoo.maxsim.model.Images;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationTemplateTest {

    private TemplateEngine engine;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        this.engine = templateEngine;
    }

    private WebContext context(Map<String, Object> variables) {
        MockServletContext servletContext = new MockServletContext();
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(servletContext);
        IWebExchange exchange = application.buildExchange(
                new MockHttpServletRequest(servletContext),
                new MockHttpServletResponse());
        return new WebContext(exchange, Locale.ROOT, variables);
    }

    private PageResult<Images> pageResult() {
        Images image = new Images();
        image.setId(1L);
        image.setImageName("image.jpg");
        image.setDescription("Описание");
        image.setCategory("Реализм");
        image.setFlag(true);
        return PageResult.of(new PageImpl<>(List.of(image), PageRequest.of(1, 9), 20), 3);
    }

    @Test
    void gridRendersAdminControlsWithoutInlineOnClick() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("page", pageResult());

        String html = engine.process("test/grid-wrapper", context(variables));

        assertThat(html).contains("gallery-item", "data-delete-item", "galleryGridContainer");
        assertThat(html).doesNotContain("onclick=\"deleteImage");
    }

    @Test
    void paginationRendersRealLinksAndAria() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("page", pageResult());
        variables.put("baseUrl", "/gallery");
        variables.put("target", ".galleryFilter");
        variables.put("style", "");

        String html = engine.process("test/pagination-wrapper", context(variables));

        assertThat(html).contains("data-pagination-link", "aria-current=\"page\"", "/gallery?page=");
        assertThat(html).contains("href=\"/gallery");
    }
}
