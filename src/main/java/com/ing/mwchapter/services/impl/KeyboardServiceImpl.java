package com.ing.mwchapter.services.impl;

import com.ing.mwchapter.services.IKeyboardService;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.StrictMath.abs;

public class KeyboardServiceImpl implements IKeyboardService {

    private static final int KEYBOARD_WIDTH = 3;
    private static final int KEYBOARD_HEIGHT = 3;
    private static final int MOVE_TO_FIRST_KEY_TIME = 0;
    private static final int MOVE_TO_ADJACENT_KEY_TIME = 1;
    private static final int PRESS_KEY_TIME = 0;

    @Override
    public int entryTime(@NotNull String code, @NotNull String keypad) {
        Map<Integer, Position> keypadMapped = buildKeypadMap(keypad);
        List<Integer> codeMapped = mapStringToList(code);
        return MOVE_TO_FIRST_KEY_TIME +
                IntStream.range(0, codeMapped.size() - 1)
                        .map(i -> PRESS_KEY_TIME + calculateMovementCost(codeMapped.get(i), codeMapped.get(i + 1), keypadMapped))
                        .sum();
    }

    private Map<Integer, Position> buildKeypadMap(String keypad) {
        List<Integer> key = filterKeymap(mapStringToList(keypad));
        checkKeypad(key);
        return createKeymapMap(key);
    }

    private void checkKeypad(List<Integer> keymapList) {
        if (keymapList.size() != KEYBOARD_HEIGHT * KEYBOARD_WIDTH) throw new IllegalArgumentException();
    }
    private List<Integer> filterKeymap(List<Integer> keymapListed) {
        return keymapListed.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Integer> mapStringToList(String string) {
        return Arrays.stream(string.split(""))
                .map(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException();
                    }
                })
                .collect(Collectors.toList());
    }


    private Map<Integer, Position> createKeymapMap(List<Integer> key) {
        List<Position> map = this.buildKeymapPositionList(key);
        return IntStream.range(0, key.size())
                .boxed()
                .collect(Collectors.toMap(key::get, map::get));
    }


    private List<Position> buildKeymapPositionList(List<Integer> keymapList) {
        return IntStream.range(0, KEYBOARD_HEIGHT)
                .mapToObj(i ->
                        IntStream.range(0, KEYBOARD_WIDTH)
                                .mapToObj(j -> new Position(i, j))
                                .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private int calculateMovementCost(int origin, int dest, Map<Integer, Position> keymap) {
        return MOVE_TO_ADJACENT_KEY_TIME * calcDistance(keymap.get(origin), keymap.get(dest));
    }

    private int calcDistance(Position originPos, Position destPos) {
        return max(abs(originPos.getX() - destPos.getX()), abs(originPos.getY() - destPos.getY()));
    }

    private static class Position {
        private final int x;
        private final int y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }

}
