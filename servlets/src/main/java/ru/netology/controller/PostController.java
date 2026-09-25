package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class PostController {
  public static final String APPLICATION_JSON = "application/json";
  private final Gson gson = new Gson();
  private final PostService service;

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    respond(response, service.all());
  }

  public void getById(long id, HttpServletResponse response) throws IOException {
    respond(response, service.getById(id));
  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    final var post = gson.fromJson(body, Post.class);
    respond(response, service.save(post));
  }

  public void removeById(long id, HttpServletResponse response) throws IOException {
    service.removeById(id);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private void respond(HttpServletResponse response, Object data) throws IOException {
    response.setContentType(APPLICATION_JSON);
    response.getWriter().print(gson.toJson(data));
  }
}
