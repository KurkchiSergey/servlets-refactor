package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.AppConfig;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

public class MainServlet extends HttpServlet {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    private static final String POSTS_PATH = "/api/posts";
    private static final Pattern POST_BY_ID_PATH = Pattern.compile("/api/posts/(\\d+)");

    private AnnotationConfigApplicationContext context;
    private PostController controller;

    @Override
    public void init() {

        context = new AnnotationConfigApplicationContext(AppConfig.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    public void destroy() {
        if (context != null) {
            context.close();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            // getServletPath(), в отличие от getRequestURI(), не включает context path,
            // поэтому роутинг работает независимо от того, куда задеплоен war
            // (в root или, например, в /servlets)
            final var path = req.getServletPath();
            final var method = req.getMethod();
            final var idMatcher = POST_BY_ID_PATH.matcher(path);

            // primitive routing
            if (GET.equals(method) && POSTS_PATH.equals(path)) {
                controller.all(resp);
                return;
            }
            if (GET.equals(method) && idMatcher.matches()) {
                final var id = Long.parseLong(idMatcher.group(1));
                controller.getById(id, resp);
                return;
            }
            if (POST.equals(method) && POSTS_PATH.equals(path)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (DELETE.equals(method) && idMatcher.matches()) {
                final var id = Long.parseLong(idMatcher.group(1));
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
