package org.example.atlassian.snakeGame;

/**
 *  board of size m*n
 *  snake of len X
 *
 *  moveSnake() -> up,down,left, right -> how to choose direction ? will it be input or random
 *  snake -> initial len = 3 and +1 for every 5 move
 *  end condition = snake touches itself
 *
 *  Deque -> Store snake body as set of coordinates, head --- tail
 *  move -> remove tail from end and add new coordinate at the start
 *  start location -> (0,0), (0,1), (0,2)
 *
 *  isGameOver if it moves out of grid? or what should we return in that case?
 *
 */


public interface ISnakeGame{
    boolean moveSnake(SnakeDirection direction);
    boolean isGameOver();
}
