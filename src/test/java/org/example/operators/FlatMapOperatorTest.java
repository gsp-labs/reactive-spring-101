package org.example.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FlatMapOperatorTest {

    static class Blog {
        private String blogName;
        private Integer id;
        private List<String> tags;

        public Blog(String blogName, Integer id, List<String> tags) {
            this.blogName = blogName;
            this.id = id;
            this.tags = tags;
        }

        public String getBlogName() {
            return blogName;
        }

        public Integer getId() {
            return id;
        }

        public List<String> getTags() {
            return tags;
        }

        @Override
        public String toString() {
            return "Blog{" +
                "blogName='" + blogName + '\'' +
                ", id='" + id + '\'' +
                ", tags=" + tags +
                '}';
        }
    }

    static class BlogPostService {
        static Map<Integer, Blog> blogs = new HashMap<>(){
            {
                put(1, new Blog("blog1", 1, Lists.newArrayList("aa", "bb", "cc")));
                put(2, new Blog("blog2", 2, Lists.newArrayList("aa", "dd", "ee")));
                put(3, new Blog("blog3", 3, Lists.newArrayList( "dd", "gg")));
                put(4, new Blog("blog4", 4, Lists.newArrayList( "bb", "ff", "tt")));
            }
        };

        public Flux<String> fetchBlogTags(Integer postId) {
            return Flux.fromStream(blogs.get(postId).getTags().stream());
        }
    }

    @Test
    public void shouldFetchDistinctTagsOfBlogPosts() {
        Flux<Integer> postIds = Flux.just(1, 2, 3).log();

        BlogPostService blogPostService = new BlogPostService();
        Flux<String> tags = postIds.flatMap(blogPostService::fetchBlogTags).distinct();

        StepVerifier.create(tags)
            .expectNextMatches(tag -> tag.equals("aa"))
            .expectNextMatches(tag -> tag.equals("bb"))
            .expectNextMatches(tag -> tag.equals("cc"))
            .expectNextMatches(tag -> tag.equals("dd"))
            .expectNextMatches(tag -> tag.equals("ee"))
            .expectNextMatches(tag -> tag.equals("gg"))
            .verifyComplete();
    }
}
