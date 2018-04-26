
import java.util.ArrayList;
import java.util.List;

import reactor.core.publisher.Flux;

public class PathSum {
  public Flux<List> findPaths(Tree tree, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    addPathRecursively(tree, new ArrayList<>(), paths, sum);
    return Flux.fromIterable(paths);
  }

  private void addPathRecursively(Tree node, List<Integer> path, List<List<Integer>> paths, int sum) {
    if (node != null) {
      path.add(node.value);
      if (node.left == null && node.right == null && sum(path) == sum) {
        paths.add(path);
      } else {
        addPathRecursively(node.left, new ArrayList<>(path), paths, sum);
        addPathRecursively(node.right, new ArrayList<>(path), paths, sum);
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
