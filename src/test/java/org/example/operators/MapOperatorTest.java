package org.example.operators;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class MapOperatorTest {

  static class User {
    private String username;

    public User(String username) {
      this.username = username;
    }

    @Override
    public String toString() {
      return "User{" +
          "username='" + username + '\'' +
          '}';
    }

    public String getUsername() {
      return username;
    }
  }

  static class UserService {
    public User getUserByUsername(String username) {
      return new User(username);
    }
  }

  @Test
  public void shouldTransformListOfStringsToUser() {
    Flux<String> usernames = Flux.just("user1", "user2", "user3").log();

    UserService userService = new UserService();
    Flux<User> users = usernames.map(userService::getUserByUsername);

    StepVerifier.create(users)
        .expectNextMatches(user -> user.getUsername().equals("user1"))
        .expectNextMatches(user -> user.getUsername().equals("user2"))
        .expectNextMatches(user -> user.getUsername().equals("user3"))
        .verifyComplete();
  }
}
