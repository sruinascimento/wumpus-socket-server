package br.com.rsfot.main;

import br.com.rsfot.domain.Direction;
import br.com.rsfot.game.HuntWumpus;

import java.util.Scanner;

import static br.com.rsfot.domain.Rotation.LEFT;
import static br.com.rsfot.domain.Rotation.RIGHT;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HuntWumpus huntWumpus = new HuntWumpus();

        boolean gameOver = false;
        while (!gameOver) {

            if (huntWumpus.isTheAgentDead()) {
                System.out.println("You died - GAME OVER");
                gameOver = true;
                continue;
            }

            if (huntWumpus.getAgent().agentWinTheGame()) {
                System.out.println("You win - GAME OVER");
                gameOver = true;
                continue;
            }
            huntWumpus.getEnvironment().showCave();
            System.out.println(menu());
            System.out.println(huntWumpus.getAgent());
            System.out.print(huntWumpus.getEnvironment().getFeelingsByCoordinate());
            System.out.println(" -> Feelings[%d,%d]:".formatted(huntWumpus.getAgent().getCoordinateX(), huntWumpus.getAgent().getCoordinateY()));
            System.out.println(huntWumpus.getEnvironment().getFeelingsByCoordinate().get(huntWumpus.getAgent().getStringCoordinate()));
//            int option = Integer.parseInt(scanner.nextLine());
            String option = scanner.nextLine();
            if (option.contains("1")) {
                Direction direction = Direction.valueOf(option.split(" ")[1]);
                huntWumpus.moveToDirection(direction);
            } else if ("2".equals(option)) {
                huntWumpus.grabGold();
            } else if ("3".equals(option)) {
//                huntWumpus.shoot();
            } else if ("4".equals(option)) {
                gameOver = true;
            } else {
                System.out.println("Invalid option");

            }


//            switch (option) {
//                case 1:
//                    huntWumpus.turnAgentTo(LEFT);
//                    break;
//                case 2:
//                    huntWumpus.turnAgentTo(RIGHT);
//                    break;
//                case 3:
//                    huntWumpus.moveForward();
//                    break;
//                case 4:
//                    huntWumpus.grabGold();
//                    break;
//                case 5:
//                    huntWumpus.shoot();
//                    break;
//                case 6:
//                    gameOver = true;
//                    break;
//                default:
//                    System.out.println("Invalid option");
//            }
        }

        scanner.close();

        System.out.println("\nResultado final:");
        System.out.println(huntWumpus.getAgent());
        System.out.println(huntWumpus.getEnvironment().getFeelingsByCoordinate());

    }

    public static String menu() {
        return """
                1 - Move to Direction
                2 - Grab Gold
                3 - Shoot
                4 - Exit
                """;
    }
}