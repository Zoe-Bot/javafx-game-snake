package game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application{
    private static final int WIDTH = 800;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private GraphicsContext gc;
    private List<Point> snakeBody = new ArrayList();
    private Point snakeHead;
    private int foodX;
    private int foodY;
    private boolean gameOver;
    private int currentDirection;
    private int score;


    @Override
    public void start(Stage primaryStage) throws Exception {
        //add Canvas and scene
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Snake");
        primaryStage.setScene(scene);
        primaryStage.show();

        //for drawing on canvas
        gc = canvas.getGraphicsContext2D();

        //movement set correct
        scene.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            if(code == KeyCode.RIGHT || code == KeyCode.D) {
                if(currentDirection != LEFT)
                    currentDirection = RIGHT;
            } else if(code == KeyCode.LEFT || code == KeyCode.A) {
                if(currentDirection != RIGHT)
                    currentDirection = LEFT;
            } else if(code == KeyCode.UP || code == KeyCode.W) {
                if(currentDirection != DOWN)
                    currentDirection = UP;
            } else if(code == KeyCode.DOWN || code == KeyCode.S) {
                if(currentDirection != UP)
                    currentDirection = DOWN;
            }
        });

        //add body parts
        for(int i = 0; i < 3; i++) {
            snakeBody.add(new Point(5, ROWS / 2));

        }
        snakeHead = snakeBody.get(0);

        //generate food
        generateFood();

        //update
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(130), e -> run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Update Method
     * manage what happens each tick
     * @param gc makes possible draw on canvas
     */
    private void run(GraphicsContext gc) {
        //set game over
        if(gameOver) {
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Poppins", 70));
            gc.fillText("Game over", WIDTH / 3.5, HEIGHT / 2);
            return;
        }

        /* TODO: implement hit wall
        hitWall();
        System.out.println(snakeHead.x);
        System.out.println(snakeHead.y);
         */


        //draw objects
        drawBackground(gc);
        drawFood(gc);
        drawSnake(gc);
        drawScore();


        //move body parts
        for(int i = snakeBody.size()- 1; i >= 1; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        //move
        switch (currentDirection) {
            case RIGHT:
                moveRight();
                break;
            case LEFT:
                moveLeft();
                break;
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
        }

        //method calls
        gameOver();
        eatFood();
    }

    /**
     * draws the chess pattern as a background
     * @param gc makes possible draw on canvas
     */
    private void drawBackground(GraphicsContext gc) {
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                if((i + j) % 2 == 0)
                    gc.setFill(Color.GRAY);
                else
                    gc.setFill(Color.DARKGRAY);
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    /**
     * generates coordinates for food and if snake eats food it generates new stuff
     */
    private void generateFood() {
        start:
        while (true) {
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLUMNS);

            for(Point snake : snakeBody) {
                if(snake.getX() == foodX && snake.getY() == foodY)
                    continue start;
            }
            break;
        }
    }

    /**
     * draws the food
     * @param gc makes possible draw on canvas
     */
    private void drawFood(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }


    
    private void drawSnake(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRoundRect(snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 25, 25);

        for(int i = 1; i < snakeBody.size(); i++) {
            gc.fillRoundRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 20, 20);
        }
    }

    private void moveRight() {
        snakeHead.x++;
    }

    private void moveLeft() {
        snakeHead.x--;
    }

    private void moveUp() {
        snakeHead.y--;
    }

    private void moveDown() {
        snakeHead.y++;
    }

    public void gameOver() {
        //hit wall TODO: remove after implement hitWall()
        if(snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT)
            gameOver = true;

        //destroy self
        for(int i = 1; i < snakeBody.size(); i++) {
            if(snakeHead.x == snakeBody.get(i).getX() && snakeHead.y == snakeBody.get(i).getY())
                gameOver = true;
        }

    }

    /* TODO: implement method
    private void hitWall() {
        if(snakeHead.x < 0) {
            snakeHead.x = 50;
            currentDirection = LEFT;
        }

        if(snakeHead.y < 0) {

        }


        if(snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT) {

        }
    }
     */

    private void eatFood() {
        if(snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            snakeBody.add(new Point(-1, -1));
            generateFood();
            score += 5;
        }
    }

    private void drawScore() {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Poppins", 35));
        gc.fillText("Score: " + score, 10, 35);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
