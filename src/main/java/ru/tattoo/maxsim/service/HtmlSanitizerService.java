package ru.tattoo.maxsim.service;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

/**
 * Сервис для санитизации HTML-контента с использованием OWASP Java HTML Sanitizer.
 * <p>
 * Удаляет опасные теги (script, object, embed), обработчики событий (on*),
 * и вредоносные протоколы (javascript:, data:) для предотвращения XSS-атак.
 * <p>
 * Разрешённые теги для mapIframe: iframe, div, span, style, img, a (только http/https).
 */
@Service
public class HtmlSanitizerService {

    private final PolicyFactory policy;

    public HtmlSanitizerService() {
        this.policy = new HtmlPolicyBuilder()
                // Разрешённые теги
                .allowElements("iframe", "div", "span", "style", "img", "a")
                // iframe: allow src with https only, allow width, height, frameborder, allow, sandbox
                .allowAttributes("src", "width", "height", "frameborder", "allow", "sandbox")
                        .onElements("iframe")
                // a: allow href (http/https only), target, rel
                .allowAttributes("href", "target", "rel")
                        .onElements("a")
                // img: allow src, alt, width, height
                .allowAttributes("src", "alt", "width", "height")
                        .onElements("img")
                // style: allow class attribute on all elements
                .allowAttributes("class")
                        .globally()
                // Разрешить стандартные URL-протоколы (http, https)
                .allowStandardUrlProtocols()
                .toFactory();
    }

    /**
     * Санитизирует HTML-строку, удаляя опасные элементы и предотвращая XSS.
     *
     * @param html исходная HTML-строка
     * @return санитизированная строка
     */
    public String sanitize(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return policy.sanitize(html);
    }
}
