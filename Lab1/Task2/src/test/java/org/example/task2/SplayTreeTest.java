package org.example.task2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SplayTreeTest {

    @Test
    void emptyTreeOperations() {
        SplayTree tree = new SplayTree();

        assertFalse(tree.search(10));
        trace(tree, "SEARCH_START", "SEARCH_EMPTY");

        assertFalse(tree.delete(10));
        trace(tree, "DELETE_START", "DELETE_EMPTY");

        assertTrue(tree.min().isEmpty());
        trace(tree, "MIN_START", "MIN_EMPTY");

        assertTrue(tree.max().isEmpty());
        trace(tree, "MAX_START", "MAX_EMPTY");

        assertTrue(tree.isEmpty());
        assertNull(tree.getRootKey());
        assertEquals(List.of(), tree.inOrder());
        assertEquals(0, tree.height());
    }

    @Test
    void insertAndDuplicate() {
        SplayTree tree = new SplayTree();

        assertTrue(tree.insert(10));
        trace(tree, "INSERT_START", "INSERT_EMPTY_ROOT");

        assertFalse(tree.insert(10));
        trace(tree,
                "INSERT_START",
                "INSERT_DUPLICATE",
                "SPLAY_START",
                "SPLAY_END"
        );

        assertTrue(tree.insert(5));
        trace(tree,
                "INSERT_START",
                "INSERT_GO_LEFT",
                "INSERT_ATTACH_LEFT",
                "SPLAY_START",
                "SPLAY_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END"
        );

        assertTrue(tree.insert(20));
        trace(tree,
                "INSERT_START",
                "INSERT_GO_RIGHT",
                "INSERT_GO_RIGHT",
                "INSERT_ATTACH_RIGHT",
                "SPLAY_START",
                "SPLAY_ZIG_ZIG_LEFT",
                "ROTATE_LEFT",
                "ROTATE_LEFT",
                "SPLAY_END"
        );

        assertEquals(20, tree.getRootKey());
        assertEquals(List.of(5, 10, 20), tree.inOrder());
    }

    @Test
    void searchMinMaxOperations() {
        SplayTree tree = new SplayTree();

        tree.insert(10);
        tree.insert(5);
        tree.insert(20);

        assertTrue(tree.search(5));
        trace(tree,
                "SEARCH_START",
                "SEARCH_GO_LEFT",
                "SEARCH_GO_LEFT",
                "SEARCH_FOUND",
                "SPLAY_START",
                "SPLAY_ZIG_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END"
        );
        assertEquals(5, tree.getRootKey());

        assertFalse(tree.search(100));
        trace(tree,
                "SEARCH_START",
                "SEARCH_GO_RIGHT",
                "SEARCH_GO_RIGHT",
                "SEARCH_GO_RIGHT",
                "SEARCH_NOT_FOUND"
        );

        assertEquals(5, tree.min().getAsInt());
        trace(tree,
                "MIN_START",
                "MIN_FOUND",
                "SPLAY_START",
                "SPLAY_END"
        );

        assertEquals(20, tree.max().getAsInt());
        trace(tree,
                "MAX_START",
                "MAX_GO_RIGHT",
                "MAX_GO_RIGHT",
                "MAX_FOUND",
                "SPLAY_START",
                "SPLAY_ZIG_ZIG_LEFT",
                "ROTATE_LEFT",
                "ROTATE_LEFT",
                "SPLAY_END"
        );

        assertEquals(20, tree.getRootKey());
    }

    @Test
    void deleteOperations() {
        SplayTree tree = new SplayTree();

        tree.insert(10);
        tree.insert(5);
        tree.insert(20);

        assertFalse(tree.delete(100));
        trace(tree,
                "DELETE_START",
                "DELETE_GO_RIGHT",
                "DELETE_NOT_FOUND"
        );

        assertTrue(tree.delete(10));
        trace(tree,
                "DELETE_START",
                "DELETE_GO_LEFT",
                "DELETE_FOUND",
                "SPLAY_START",
                "SPLAY_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END",
                "DELETE_HAS_TWO_SUBTREES",
                "DELETE_MAX_READY",
                "SPLAY_START",
                "SPLAY_END",
                "DELETE_JOINED"
        );

        assertEquals(List.of(5, 20), tree.inOrder());

        assertTrue(tree.delete(5));
        trace(tree,
                "DELETE_START",
                "DELETE_FOUND",
                "SPLAY_START",
                "SPLAY_END",
                "DELETE_NO_LEFT"
        );

        assertEquals(List.of(20), tree.inOrder());

        assertTrue(tree.delete(20));
        trace(tree,
                "DELETE_START",
                "DELETE_FOUND",
                "SPLAY_START",
                "SPLAY_END",
                "DELETE_NO_LEFT"
        );

        assertTrue(tree.isEmpty());
    }

    @Test
    void allRotationCases() {
        SplayTree tree1 = new SplayTree();

        tree1.insert(30);
        tree1.insert(20);
        tree1.insert(40);
        tree1.insert(10);

        trace(tree1,
                "INSERT_START",
                "INSERT_GO_LEFT",
                "INSERT_GO_LEFT",
                "INSERT_GO_LEFT",
                "INSERT_ATTACH_LEFT",
                "SPLAY_START",
                "SPLAY_ZIG_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END"
        );

        SplayTree tree2 = new SplayTree();

        tree2.insert(10);
        tree2.insert(20);
        tree2.insert(0);
        tree2.insert(30);

        trace(tree2,
                "INSERT_START",
                "INSERT_GO_RIGHT",
                "INSERT_GO_RIGHT",
                "INSERT_GO_RIGHT",
                "INSERT_ATTACH_RIGHT",
                "SPLAY_START",
                "SPLAY_ZIG_ZIG_LEFT",
                "ROTATE_LEFT",
                "ROTATE_LEFT",
                "SPLAY_ZIG_LEFT",
                "ROTATE_LEFT",
                "SPLAY_END"
        );

        SplayTree tree3 = new SplayTree();

        tree3.insert(20);
        tree3.insert(10);
        tree3.insert(30);
        tree3.insert(15);

        trace(tree3,
                "INSERT_START",
                "INSERT_GO_LEFT",
                "INSERT_GO_LEFT",
                "INSERT_GO_RIGHT",
                "INSERT_ATTACH_RIGHT",
                "SPLAY_START",
                "SPLAY_ZIG_ZAG_LEFT_RIGHT",
                "ROTATE_LEFT",
                "ROTATE_RIGHT",
                "SPLAY_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END"
        );

        SplayTree tree4 = new SplayTree();

        tree4.insert(10);
        tree4.insert(20);
        tree4.insert(0);
        tree4.insert(15);

        trace(tree4,
                "INSERT_START",
                "INSERT_GO_RIGHT",
                "INSERT_GO_RIGHT",
                "INSERT_GO_LEFT",
                "INSERT_ATTACH_LEFT",
                "SPLAY_START",
                "SPLAY_ZIG_ZAG_RIGHT_LEFT",
                "ROTATE_RIGHT",
                "ROTATE_LEFT",
                "SPLAY_ZIG_LEFT",
                "ROTATE_LEFT",
                "SPLAY_END"
        );
    }

    @Test
    void deleteRootWithoutRightSubtreeCoversDeleteNoRight() {
        SplayTree tree = new SplayTree();

        tree.insert(10);
        tree.insert(20);

        assertTrue(tree.delete(20));

        trace(tree,
                "DELETE_START",
                "DELETE_FOUND",
                "SPLAY_START",
                "SPLAY_END",
                "DELETE_NO_RIGHT"
        );

        assertEquals(10, tree.getRootKey());
        assertEquals(List.of(10), tree.inOrder());
    }

    @Test
    void deleteWithMaxGoingRightCoversDeleteMaxGoRight() {
        SplayTree tree = new SplayTree();

        tree.insert(10);
        tree.insert(30);
        tree.insert(40);
        tree.insert(20);
        tree.insert(50);

        assertTrue(tree.delete(40));

        trace(tree,
                "DELETE_START",
                "DELETE_GO_LEFT",
                "DELETE_FOUND",
                "SPLAY_START",
                "SPLAY_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END",
                "DELETE_HAS_TWO_SUBTREES",
                "DELETE_MAX_GO_RIGHT",
                "DELETE_MAX_READY",
                "SPLAY_START",
                "SPLAY_ZIG_LEFT",
                "ROTATE_LEFT",
                "SPLAY_END",
                "DELETE_JOINED"
        );

        assertEquals(30, tree.getRootKey());
        assertEquals(List.of(10, 20, 30, 50), tree.inOrder());
    }

    @Test
    void minWithGoingLeftCoversMinGoLeft() {
        SplayTree tree = new SplayTree();

        tree.insert(10);
        tree.insert(5);
        tree.insert(20);

        assertEquals(5, tree.min().getAsInt());

        trace(tree,
                "MIN_START",
                "MIN_GO_LEFT",
                "MIN_GO_LEFT",
                "MIN_FOUND",
                "SPLAY_START",
                "SPLAY_ZIG_ZIG_RIGHT",
                "ROTATE_RIGHT",
                "ROTATE_RIGHT",
                "SPLAY_END"
        );

        assertEquals(5, tree.getRootKey());
    }

    @Test
    void heightCoversRecursiveCalculation() {
        SplayTree tree = new SplayTree();

        tree.insert(10);
        tree.insert(5);
        tree.insert(20);

        assertEquals(3, tree.height());
    }

    private static void trace(SplayTree tree, String... expected) {
        assertEquals(List.of(expected), tree.getTrace());
    }
}