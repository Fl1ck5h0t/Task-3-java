package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dao.PlayerDAO;
import org.example.pojo.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerService {

    private  Map<Long, Player> cache;

    private final PlayerDAO playerDAO;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlayerService(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
        upload();
    }
    public void upload() {
        try {
            System.out.println("Uploading to DB");
            List<Player> players = parseJsonFile();
            players.forEach(playerDAO::insert);
            System.out.println(playerDAO.getAll().size());
            cache = playerDAO
                    .getAll()
                    .stream()
                    .collect(Collectors.toMap(Player::getPlayerId, Function.identity()));
            System.out.println("Upload successful, cache size: " + cache.values().size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Player> parseJsonFile() throws IOException {
        String jsonFileName = "players.json";
        InputStream jsonFile = Objects.requireNonNull(PlayerService.class.getClassLoader().getResourceAsStream(jsonFileName));
        return objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class));
    }

    public Map<Long, Player> getCache() {
        return cache;
    }
}
