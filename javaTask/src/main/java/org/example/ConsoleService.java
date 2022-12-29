package org.example;

import org.example.dao.CurrencyDAO;
import org.example.dao.ItemDAO;
import org.example.dao.PlayerDAO;
import org.example.dao.ProgressDAO;
import org.example.pojo.Currency;
import org.example.pojo.Item;
import org.example.pojo.Player;
import org.example.pojo.Progress;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import static org.example.ConsoleOperations.READ;

public class ConsoleService {

    private final PlayerDAO playerDAO;

    public ConsoleService(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    public void readConsole(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Print --create for creating player, --update for updating, --read for reading, --delete for deleting");
        String operationString = scanner.next();
        ConsoleOperations operation = ConsoleOperations.valueOfOparation(operationString);
        switch (operation){
            case READ    ->{
                System.out.println("Print id: ");
                Long id = scanner.nextLong();
                Optional<Player> player = playerDAO.getById(id);
                System.out.println(player.orElseGet(null).toString());
            }
            case CREATE -> {
                System.out.println("Print id: ");
                Long id = scanner.nextLong();
                System.out.println("Print nickname: ");
                String nickname = scanner.next();
                Map<Long,Progress> progressMap = readProgresses(scanner, id);
                Map<Long,Item> itemsMap = readItems(scanner);
                Map<Long,Currency> currencyMap = readCurrency(scanner);
                Player player = Player.builder()
                        .playerId(id)
                        .nickname(nickname)
                        .currencies(currencyMap)
                        .items(itemsMap)
                        .progresses(progressMap.values().stream().toList())
                        .build();
                playerDAO.insert(player);
            }
            case UPDATE -> {
                System.out.println("Print id: ");
                Long id = scanner.nextLong();
                System.out.println("Print nickname: ");
                String nickname = scanner.next();
                Player player = Player.builder()
                        .playerId(id)
                        .nickname(nickname)
                        .build();
                playerDAO.update(player);
            }
            case DELETE -> {
                System.out.println("Print id: ");
                Long id = scanner.nextLong();
                playerDAO.delete(id);
            }
        }
    }

    private Map<Long, Progress> readProgresses(Scanner scanner, Long playerId) {
        Map<Long, Progress> result = new HashMap<>();
        System.out.print("How many progresses do you want to add: ");
        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.println("-------------------");
            System.out.print("id: ");
            Long id = scanner.nextLong();
            System.out.print("resourceId: ");
            Long resourceId = scanner.nextLong();
            System.out.print("score: ");
            int score = scanner.nextInt();
            System.out.print("maxScore: ");
            int maxScore = scanner.nextInt();

            Progress progress = Progress
                    .builder()
                    .id(id)
                    .playerId(playerId)
                    .resourceId(resourceId)
                    .score(score)
                    .maxScore(maxScore)
                    .build();
            result.put(id, progress);
        }
        return result;
    }

    private Map<Long, Item> readItems(Scanner scanner) {
        Map<Long, Item> result = new HashMap<>();
        System.out.print("How many items do you want to add: ");
        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.println("-------------------");
            System.out.print("id: ");
            Long id = scanner.nextLong();
            System.out.print("resourceId: ");
            Long resourceId = scanner.nextLong();
            System.out.print("level: ");
            int level = scanner.nextInt();
            System.out.print("count: ");
            int count = scanner.nextInt();

            Item item = Item
                    .builder()
                    .count(count)
                    .level(level)
                    .resourceId(resourceId)
                    .id(id)
                    .build();
            result.put(id, item);
        }
        return result;
    }

    private Map<Long, Currency> readCurrency(Scanner scanner) {
        Map<Long, Currency> result = new HashMap<>();
        System.out.print("How many currencies do you want to add: ");
        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            System.out.println("-------------------");
            System.out.print("id: ");
            Long id = scanner.nextLong();
            System.out.print("resourceId: ");
            Long resourceId = scanner.nextLong();
            System.out.print("name: ");
            String name = scanner.next();
            System.out.print("count: ");
            int count = scanner.nextInt();

            Currency currency = Currency
                    .builder()
                    .id(id)
                    .resourceId(resourceId)
                    .name(name)
                    .count(count)
                    .build();
            result.put(id, currency);
        }
        return result;
    }
}
