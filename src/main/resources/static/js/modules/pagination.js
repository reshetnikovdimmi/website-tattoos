/**
 * Прогрессивная пагинация и фильтры для галереи/эскизов.
 *
 * Ссылки и формы работают без JS; модуль лишь перехватывает клик/отправку,
 * запрашивает HTML-фрагмент и обновляет data-target.
 */
(function (window, $) {
    'use strict';

    if (!$) {
        return;
    }

    var pending = new Map();

    function cleanUrl(url) {
        try {
            var parsed = new URL(url, window.location.origin);
            parsed.searchParams.delete('fragment');
            return parsed.pathname + parsed.search + parsed.hash;
        } catch (e) {
            return url;
        }
    }

    function fragmentUrl(url) {
        var parsed = new URL(url, window.location.origin);
        parsed.searchParams.set('fragment', 'grid');
        return parsed.pathname + parsed.search;
    }

    function setActiveFilter(style) {
        $('.gallery-controls li').each(function () {
            var $link = $(this).find('[data-filter-link]');
            var value = $link.attr('data-filter-style') || '';
            $(this).toggleClass('active', value === (style || ''));
        });
    }

    function reinitWidgets() {
        if (typeof window.initMagnificPopup === 'function') {
            window.initMagnificPopup();
        }
        if (typeof window.reinitSetBg === 'function') {
            window.reinitSetBg();
        }
    }

    function showLoading(target, loading) {
        var $target = $(target);
        if (!$target.length) {
            return;
        }
        $target.toggleClass('is-loading', loading);
        if (loading) {
            $target.attr('aria-busy', 'true');
        } else {
            $target.removeAttr('aria-busy');
        }
    }

    function load(url, target, pushState) {
        if (!target || !$(target).length) {
            window.location.href = cleanUrl(url);
            return;
        }

        if (pending.has(target)) {
            pending.get(target).abort();
        }

        var controller = new AbortController();
        pending.set(target, controller);
        showLoading(target, true);

        fetch(fragmentUrl(url), {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'text/html'
            },
            signal: controller.signal
        }).then(function (response) {
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            return response.text();
        }).then(function (html) {
            $(target).html(html);
            reinitWidgets();
            if (pushState) {
                window.history.pushState(
                    {url: cleanUrl(url), target: target},
                    '',
                    cleanUrl(url)
                );
            }
        }).catch(function (error) {
            if (error.name === 'AbortError') {
                return;
            }
            console.error('Pagination request failed:', error);
            // Без AJAX ссылка должна привести на полноценную страницу.
            window.location.href = cleanUrl(url);
        }).finally(function () {
            showLoading(target, false);
            if (pending.get(target) === controller) {
                pending.delete(target);
            }
        });
    }

    $(document).on('click', '[data-pagination-link], [data-filter-link]', function (event) {
        if (event.which !== 1 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
            return;
        }

        var href = this.getAttribute('href');
        var target = $(this).data('target');
        if (!href || !target) {
            return;
        }

        event.preventDefault();
        if ($(this).is('[data-filter-link]')) {
            setActiveFilter(this.getAttribute('data-filter-style') || '');
        }
        load(href, target, true);
    });

    $(document).on('submit', '[data-page-size-form]', function (event) {
        var $form = $(this);
        var action = $form.attr('action');
        var target = $form.data('target');
        if (!action || !target) {
            return;
        }

        event.preventDefault();

        var url = new URL(action, window.location.origin);
        url.searchParams.set('page', '0');
        url.searchParams.set('size', $form.find('[name="size"]').val());

        var style = $form.find('[name="style"]').val();
        if (style) {
            url.searchParams.set('style', style);
        } else {
            url.searchParams.delete('style');
        }

        load(url.pathname + url.search, target, true);
    });

    window.addEventListener('popstate', function (event) {
        var currentStyle = new URL(window.location.href).searchParams.get('style') || '';
        setActiveFilter(currentStyle);
        if (event.state && event.state.url && event.state.target) {
            load(event.state.url, event.state.target, false);
        } else {
            window.location.reload();
        }
    });

    window.Pagination = {
        load: load,
        cleanUrl: cleanUrl
    };
}(window, window.jQuery));
