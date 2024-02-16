package org.example.operators;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class SwitchMapOperatorTest {

    static class SearchResult {
        private String query;
        private String result;

        public String getQuery() {
            return query;
        }

        public String getResult() {
            return result;
        }

        public SearchResult(String query, String result) {
            this.query = query;
            this.result = result;
        }

        @Override
        public String toString() {
            return "SearchResult{" +
                "query='" + query + '\'' +
                ", result='" + result + '\'' +
                '}';
        }
    }

    static class SearchService {
        public Flux<String> search(String query) {
            return Flux.just("Result for query " + query);
        }
    }

    @Test
    public void shouldDo() {
        Flux<String> searchQueries = Flux.just("query1", "query2", "query3");

        SearchService searchService = new SearchService();
        Flux<SearchResult> searchResults = searchQueries.switchMap(query ->
            searchService.search(query)
                .delayElements(Duration.ofMillis(100)) // Simulate asynchronous processing
                .map(result -> new SearchResult(query, result))
                .doOnSubscribe(subscription -> System.out.println("Search started for query: " + query))
                .doOnNext(result -> System.out.println("Search completed for query: " + query))
        ).log();


        StepVerifier.create(searchResults)
            .expectNextMatches(searchResult -> searchResult.getResult().equals("Result for query query3"))
            .verifyComplete();
    }
}
