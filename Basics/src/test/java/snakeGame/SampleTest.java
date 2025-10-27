package snakeGame;

import org.example.atlassian.snakeGame.SnakeDirection;
import org.example.atlassian.snakeGame.SnakeGame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SampleTest {

    private static SnakeGame snakeGame;
    @BeforeAll
    public static void init(){
        snakeGame = new SnakeGame(3,3);
    }

    @Test
    public void moveDown(){
        assertEquals(true, snakeGame.moveSnake(SnakeDirection.DOWN));
    }
    @Test
    public void moveRight(){
        assertThrows(RuntimeException.class, () -> snakeGame.moveSnake(SnakeDirection.LEFT));
    }


@Test
    public void testRandomMovement() throws InterruptedException {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        int rows = 3;
        int columns = 4;

        for (int i = 0; i < rows * columns; i++) {
            if (!snakeGame.isGameOver()){
                SnakeDirection[] directions = SnakeDirection.values();
                int randomIndex = new java.util.Random().nextInt(directions.length);
                scheduledExecutorService.scheduleWithFixedDelay(() -> snakeGame.moveSnake(directions[randomIndex]), 0, 1, TimeUnit.SECONDS);
            } else{
                break;
            }
        }

        Thread.sleep(5000);
    }
}
