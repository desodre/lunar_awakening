package com.lunarawakening.spawn;

import java.util.ArrayDeque;

public final class SpawnCapContext {
    private static final ThreadLocal<ArrayDeque<Boolean>> ACTIVE =
            ThreadLocal.withInitial(ArrayDeque::new);

    private SpawnCapContext() {}

    public static void push(boolean active) {
        ACTIVE.get().push(active);
    }

    public static void pop() {
        ArrayDeque<Boolean> stack = ACTIVE.get();
        stack.pop();
        if (stack.isEmpty()) ACTIVE.remove();
    }

    public static boolean active() {
        ArrayDeque<Boolean> stack = ACTIVE.get();
        return !stack.isEmpty() && stack.peek();
    }
}
