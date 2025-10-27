package org.example.atlassian.snakeGame;

import java.util.*;

public class SnakeGame implements ISnakeGame{
    private int moves;
    private int rows, cols;
    private Deque<Point> snake;
    private HashSet<Point> snakeBody;
    private Boolean isGameOver;

    public SnakeGame(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.moves = 0;
        this.snake = new ArrayDeque<>();
        this.snakeBody = new HashSet<>();
        List<Point> points = List.of(new Point(0,0), new Point(0,1), new Point(0,2));
        for(Point point: points){
            snakeBody.add(point);
        }
        snake.addAll(points);
        this.isGameOver = false;
    }

    @Override
    public boolean moveSnake(SnakeDirection direction) {
        if(direction == null){
            throw new RuntimeException("Direction cannot be null");
        }
        if(isGameOver){
            throw new RuntimeException("GAME_OVER");
        }

        moves++;
        Point currHead = snake.getFirst();
        Point newHead = new Point(currHead.getX(), currHead.getY());
        switch (direction){
            case UP:
                newHead.x-=1;
                break;
            case DOWN:
                newHead.x+=1;
                break;
            case LEFT:
                newHead.y-=1;
                break;
            case RIGHT:
                newHead.y+=1;
                break;
        }
        if(moves % 5 != 0){ // for every 5th move we keep the tail as it is
            Point currTail = snake.removeLast();
            snakeBody.remove(currTail);
        }
        System.out.println(snake.toString() + " newHead" + newHead.toString());
        if(!isValid(newHead) || snakeBody.contains(newHead)){
            // head goes outside the boundary or touches itself
            this.isGameOver = true;
            System.out.println("Game over");
            throw new RuntimeException("GAME_OVER");
        }
        snake.addFirst(newHead);
        snakeBody.add(newHead);
        return true;
    }

    private boolean isValid(Point cell){
        return cell.getX() >= 0 && cell.getX() < rows && cell.getY() >=0 && cell.getY() < cols;
    }
    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    class Point{
        private int x, y;

        public Point() {
            this.x = -1;
            this.y = -1;
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
