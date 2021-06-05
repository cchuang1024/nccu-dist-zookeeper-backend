package edu.nccu.util;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NumbersTest {

    public static final String[] strings = {"1", "2", "3"};

    @Test
    public void testToNumbers(){
        List<Integer> numbers = Numbers.toNumbers(strings);

        for(int i=0; i<strings.length; ++i){
            String string = strings[i];
            int number = numbers.get(i);

            assertThat(number).isEqualTo(Integer.parseInt(string));

            log.info("{}th is {}", i, number);
        }
    }
}
