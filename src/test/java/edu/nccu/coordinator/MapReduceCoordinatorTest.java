package edu.nccu.coordinator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.nccu.util.Numbers;
import edu.nccu.util.NumbersTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class MapReduceCoordinatorTest {

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private MapReduceCoordinator coordinator;

    @Test
    public void testLoadContext(){
        assertThat(zkClient).isNotNull();
        assertThat(coordinator).isNotNull();
    }

    @Test
    public void testSquareAndSum() throws ExecutionException, InterruptedException {
        initApp2();

        CompletableFuture<Integer> result = coordinator.squareAndSum(Numbers.toNumbers(NumbersTest.strings));
        assertThat(result.get()).isEqualTo(14);

    }

    private void initApp2() {

    }
}
