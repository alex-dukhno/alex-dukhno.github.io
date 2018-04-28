package examples;

import org.junit.Test;

import java.util.List;

import reactor.test.StepVerifier;

public class ReactiveTreeTest {
  @Test
  public void emptyStream_whenGivenEmptyTree() throws Exception {
    new PathSum().findPaths(null, 10)
        .as(StepVerifier::create)
        .verifyComplete();
  }

  @Test
  public void streamHasSingleEvent_whenTreeHasOnlyRoot_andRootValueEqualsToTargetSum() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(10), 10)
        .as(StepVerifier::create)
        .expectNext(List.of(10))
        .verifyComplete();
  }

  @Test
  public void streamIsEmpty_whenTreeHasOnlyRoot_andRootValueNotEqualsToTargetSum() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(20), 10)
        .as(StepVerifier::create)
        .verifyComplete();
  }

  @Test
  public void streamHasSingleEvent_whenTreeHasRoot_andLeftLeaf_andValuesSumEqualsToTargetSum() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(3, new PathSum.Tree(4)), 7)
        .as(StepVerifier::create)
        .expectNext(List.of(3, 4))
        .verifyComplete();
  }

  @Test
  public void streamHasTwoEvents_whenTreeHasRoot_andBothLeaves_onPaths() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(3, new PathSum.Tree(4), new PathSum.Tree(4)), 7)
        .as(StepVerifier::create)
        .expectNext(List.of(3, 4))
        .expectNext(List.of(3, 4))
        .verifyComplete();
  }

  @Test
  public void streamHasEventsOf_rootToLeafPaths() throws Exception {
    new PathSum().findPaths(new PathSum.Tree(1, new PathSum.Tree(0)), 1)
        .as(StepVerifier::create)
        .expectNext(List.of(1, 0))
        .verifyComplete();
  }
}
