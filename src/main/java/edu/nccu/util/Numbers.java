package edu.nccu.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Numbers {

    public static List<Integer> toNumbers(String[] strings) {
        return Stream.of(strings)
                     .map(Integer::parseInt)
                     .collect(Collectors.toList());
    }
}
