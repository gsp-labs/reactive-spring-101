package org.example.operators;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ConcatMapOperatorTest {

    static class User {
        private int id;
        private String name;
        private int age;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public User(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    static class UserService {
        // Simulating an external service to fetch user details by user ID
        private Map<Integer, User> userDatabase = new HashMap<>();

        public UserService() {
            // Initialize user database
            userDatabase.put(1, new User(1, "Alice", 30));
            userDatabase.put(2, new User(2, "Bob", 25));
            userDatabase.put(3, new User(3, "Charlie", 35));
        }

        public Mono<User> getUserDetails(int userId) {
            // Simulating fetching user details from external service
            return Mono.justOrEmpty(userDatabase.get(userId));
        }
    }

    @Test
    public void shouldFetchCompleteUserDetails() {
        Flux<Integer> userIds = Flux.just(1, 2, 3);

        UserService userService = new UserService();
        Flux<User> userDetails = userIds.concatMap(userService::getUserDetails).log();

        StepVerifier.create(userDetails)
            .expectNextMatches(user -> user.getId() == 1 && user.getAge() == 30 && user.getName().equals("Alice"))
            .expectNextMatches(user -> user.getId() == 2 && user.getAge() == 25 && user.getName().equals("Bob"))
            .expectNextMatches(user -> user.getId() == 3 && user.getAge() == 35 && user.getName().equals("Charlie"))
            .verifyComplete();
    }
}
