package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;


public class PostRepository {
  private final ConcurrentSkipListMap<Long, Post> posts = new ConcurrentSkipListMap<>();
  private final AtomicLong idCounter = new AtomicLong(0);

  public List<Post> all() {
    return new ArrayList<>(posts.values());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  public Post save(Post post) {
    if (post.getId() == 0) {
      // creation: hand out a fresh id and store the post under it
      final var id = idCounter.incrementAndGet();
      post.setId(id);
      posts.put(id, post);
      return post;
    }

    final var updated = posts.computeIfPresent(post.getId(), (id, existing) -> post);
    if (updated == null) {
      throw new NotFoundException("Post with id = " + post.getId() + " not found");
    }
    return updated;
  }

  public void removeById(long id) {
    if (posts.remove(id) == null) {
      throw new NotFoundException("Post with id = " + id + " not found");
    }
  }
}
