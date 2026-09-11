package org.example.task2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public class SplayTree {

    private Node root;
    private final List<String> trace = new ArrayList<>();

    private static class Node {
        int key;
        Node left;
        Node right;
        Node parent;

        Node(int key) {
            this.key = key;
        }
    }

    /**
     * Возвращает копию трассы выполнения последнего вызванного метода.
     */
    public List<String> getTrace() {
        return Collections.unmodifiableList(trace);
    }

    /**
     * Проверка: пустое ли дерево.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Возвращает ключ корня.
     * Если дерево пустое, возвращает null.
     */
    public Integer getRootKey() {
        return root == null ? null : root.key;
    }

    /**
     * Вставка элемента в Splay Tree.
     * После вставки новый элемент поднимается в корень.
     */
    public boolean insert(int key) {
        trace.clear();
        mark("INSERT_START");

        if (root == null) {
            root = new Node(key);
            mark("INSERT_EMPTY_ROOT");
            return true;
        }

        Node current = root;
        Node parent = null;

        while (current != null) {
            parent = current;

            if (key < current.key) {
                mark("INSERT_GO_LEFT");
                current = current.left;
            } else if (key > current.key) {
                mark("INSERT_GO_RIGHT");
                current = current.right;
            } else {
                mark("INSERT_DUPLICATE");
                splay(current);
                return false;
            }
        }

        Node newNode = new Node(key);
        newNode.parent = parent;

        if (key < parent.key) {
            parent.left = newNode;
            mark("INSERT_ATTACH_LEFT");
        } else {
            parent.right = newNode;
            mark("INSERT_ATTACH_RIGHT");
        }

        splay(newNode);
        return true;
    }

    /**
     * Поиск элемента.
     * Если элемент найден, он поднимается в корень.
     */
    public boolean search(int key) {
        trace.clear();
        mark("SEARCH_START");

        if (root == null) {
            mark("SEARCH_EMPTY");
            return false;
        }

        Node current = root;

        while (current != null) {
            if (key == current.key) {
                mark("SEARCH_FOUND");
                splay(current);
                return true;
            }

            if (key < current.key) {
                mark("SEARCH_GO_LEFT");
                current = current.left;
            } else {
                mark("SEARCH_GO_RIGHT");
                current = current.right;
            }
        }

        mark("SEARCH_NOT_FOUND");
        return false;
    }

    /**
     * Удаление элемента.
     * Если элемент найден, сначала поднимаем его в корень.
     * Затем соединяем левое и правое поддерево.
     */
    public boolean delete(int key) {
        trace.clear();
        mark("DELETE_START");

        if (root == null) {
            mark("DELETE_EMPTY");
            return false;
        }

        Node current = root;

        while (current != null && current.key != key) {
            if (key < current.key) {
                mark("DELETE_GO_LEFT");
                current = current.left;
            } else {
                mark("DELETE_GO_RIGHT");
                current = current.right;
            }
        }

        if (current == null) {
            mark("DELETE_NOT_FOUND");
            return false;
        }

        mark("DELETE_FOUND");
        splay(current);

        Node leftSubtree = root.left;
        Node rightSubtree = root.right;

        if (leftSubtree == null) {
            mark("DELETE_NO_LEFT");
            root = rightSubtree;

            if (root != null) {
                root.parent = null;
            }

            return true;
        }

        if (rightSubtree == null) {
            mark("DELETE_NO_RIGHT");
            root = leftSubtree;
            root.parent = null;
            return true;
        }

        mark("DELETE_HAS_TWO_SUBTREES");

        leftSubtree.parent = null;
        rightSubtree.parent = null;

        root = leftSubtree;

        Node maxInLeftSubtree = leftSubtree;

        while (maxInLeftSubtree.right != null) {
            mark("DELETE_MAX_GO_RIGHT");
            maxInLeftSubtree = maxInLeftSubtree.right;
        }

        mark("DELETE_MAX_READY");

        splay(maxInLeftSubtree);

        root.right = rightSubtree;
        rightSubtree.parent = root;

        mark("DELETE_JOINED");
        return true;
    }

    public OptionalInt min() {
        trace.clear();
        mark("MIN_START");

        if (root == null) {
            mark("MIN_EMPTY");
            return OptionalInt.empty();
        }

        Node current = root;

        while (current.left != null) {
            mark("MIN_GO_LEFT");
            current = current.left;
        }

        mark("MIN_FOUND");
        splay(current);

        return OptionalInt.of(current.key);
    }

    public OptionalInt max() {
        trace.clear();
        mark("MAX_START");

        if (root == null) {
            mark("MAX_EMPTY");
            return OptionalInt.empty();
        }

        Node current = root;

        while (current.right != null) {
            mark("MAX_GO_RIGHT");
            current = current.right;
        }

        mark("MAX_FOUND");
        splay(current);

        return OptionalInt.of(current.key);
    }

    /**
     * Симметричный обход дерева.
     * Нужен в тестах, чтобы проверить корректность структуры.
     */
    public List<Integer> inOrder() {
        List<Integer> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node node, List<Integer> result) {
        if (node == null) {
            return;
        }

        inOrder(node.left, result);
        result.add(node.key);
        inOrder(node.right, result);
    }

    /**
     * Высота дерева.
     * Пустое дерево имеет высоту 0.
     */
    public int height() {
        return height(root);
    }

    private int height(Node node) {
        if (node == null) {
            return 0;
        }

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * Главная операция Splay Tree дерева.
     * Поднимает заданный узел в корень.
     */
    private void splay(Node node) {
        mark("SPLAY_START");

        while (node.parent != null) {
            Node parent = node.parent;
            Node grandparent = parent.parent;

            if (grandparent == null) {
                if (node == parent.left) {
                    mark("SPLAY_ZIG_RIGHT");
                    rotateRight(parent);
                } else {
                    mark("SPLAY_ZIG_LEFT");
                    rotateLeft(parent);
                }
            } else if (node == parent.left && parent == grandparent.left) {
                mark("SPLAY_ZIG_ZIG_RIGHT");
                rotateRight(grandparent);
                rotateRight(parent);
            } else if (node == parent.right && parent == grandparent.right) {
                mark("SPLAY_ZIG_ZIG_LEFT");
                rotateLeft(grandparent);
                rotateLeft(parent);
            } else if (node == parent.right && parent == grandparent.left) {
                mark("SPLAY_ZIG_ZAG_LEFT_RIGHT");
                rotateLeft(parent);
                rotateRight(grandparent);
            } else {
                mark("SPLAY_ZIG_ZAG_RIGHT_LEFT");
                rotateRight(parent);
                rotateLeft(grandparent);
            }
        }

        mark("SPLAY_END");
    }

    /**
     * Левый поворот.
     *
     * Было:
     *     x
     *      \
     *       y
     *
     * Стало:
     *       y
     *      /
     *     x
     */
    private void rotateLeft(Node x) {
        mark("ROTATE_LEFT");

        Node y = x.right;

        x.right = y.left;

        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    /**
     * Правый поворот.
     *
     * Было:
     *       x
     *      /
     *     y
     *
     * Стало:
     *     y
     *      \
     *       x
     */
    private void rotateRight(Node x) {
        mark("ROTATE_RIGHT");

        Node y = x.left;

        x.left = y.right;

        if (y.right != null) {
            y.right.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.right = x;
        x.parent = y;
    }

    private void mark(String point) {
        trace.add(point);
    }
}