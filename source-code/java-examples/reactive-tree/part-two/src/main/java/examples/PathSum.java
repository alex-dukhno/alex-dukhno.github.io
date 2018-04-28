package examples;

import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class PathSum {
  public Flux<List<Integer>> findPaths(Tree tree, int sum) {
    return Flux.<List<Integer>>create(
        emitter -> {
          addPathRecursively(tree, new ArrayList<>(), sum, emitter);
          emitter.complete();
        }
    );
  }

  private void addPathRecursively(Tree node, List<Integer> path, int sum, FluxSink<List<Integer>> emitter) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        emitter.next(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), sum, emitter);
        addPathRecursively(node.right, new ArrayList<>(path), sum, emitter);
      }
    }
  }

  private int sum(List<Integer> path) {
    int sum = 0;
    for (int e : path) {
      sum += e;
    }
    return sum;
  }

  public static class Tree {
    int value;
    Tree left;
    Tree right;

    public Tree(int value) {
      this.value = value;
    }

    public Tree(int value, Tree left) {
      this(value);
      this.left = left;
    }

    public Tree(int value, Tree left, Tree right) {
      this(value, left);
      this.right = right;
    }
  }
}
