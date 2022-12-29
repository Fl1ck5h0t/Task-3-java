package org.example.dao;

import org.example.pojo.Currency;
import org.example.pojo.Item;
import org.example.pojo.Player;
import org.example.pojo.Progress;

import java.sql.*;
import java.util.*;

import static org.example.config.ConnectionValues.*;

public class PlayerDAO implements CrudDAO<Player, Long> {
    private final ProgressDAO progressDAO;
    private final ItemDAO itemDAO;
    private final CurrencyDAO currencyDAO;
    public PlayerDAO(ProgressDAO progressDAO, ItemDAO itemDAO, CurrencyDAO currencyDAO) {
        this.progressDAO = progressDAO;
        this.itemDAO = itemDAO;
        this.currencyDAO = currencyDAO;
    }

    @Override
    public Player insert(Player player) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players(playerId, nickname) VALUES (?, ?)");
            preparedStatement.setLong(1, player.getPlayerId());
            preparedStatement.setString(2, player.getNickname());
            preparedStatement.executeUpdate();
            player.getProgresses().forEach(progressDAO::insert);
            for (var item : player.getItems().values()) {
                if(itemDAO.existById(item.getId())){
                    itemDAO.update(item);
                }else{
                    itemDAO.insert(item);
                }
                if (!existsInPlayerItemMap(connection, player.getPlayerId(), item.getId())) {
                    insertIntoPlayerItemMap(connection, player.getPlayerId(), item.getId());
                }
            }
            for (var currency : player.getCurrencies().values()) {
                if(currencyDAO.existById(currency.getId())){
                    currencyDAO.update(currency);
                }else{
                    currencyDAO.insert(currency);
                }
                if (!existsInPlayerCurrencyMap(connection, player.getPlayerId(), currency.getId())) {
                    insertIntoPlayerCurrencyMap(connection, player.getPlayerId(), currency.getId());
                }
            }
            return player;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player update(Player player) {
        if (player.getPlayerId() == null || getById(player.getPlayerId()).isEmpty()) {
            throw new RuntimeException();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET nickname = ? where playerid = ?;");
            preparedStatement.setLong(1, player.getPlayerId());
            preparedStatement.setString(2, player.getNickname());
            preparedStatement.executeUpdate();
            return player;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }


    @Override
    public Optional<Player> getById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT pl.playerid, pl.nickname, " +
                    "pr.id as pr_id, pr.score as pr_score, pr.maxscore as pr_maxscore, pr.resourceid as pr_resource_id, " +
                    "i.id as i_id, i.count as i_count, i.level as i_level, i.resourceid as i_resource_id, " +
                    "c.id as c_id, c.resourceid as c_resource_id, c.count as c_count, c.name as c_name " +
                    "FROM players pl " +
                    "left join progresses pr on pl.playerid = pr.playerid " +
                    "left join player_item_map pim on pl.playerid = pim.playerid " +
                    "left join items i on pim.itemid = i.id " +
                    "left join player_currency_map pcm on pl.playerid = pcm.playerid " +
                    "left join currencies c on c.id = pcm.currencyid " +
                    "where pl.playerid = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return Objects.requireNonNull(convertToPlayers(preparedStatement.getResultSet())).stream().findAny();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Player> getAll() {
        List<Player> players = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT pl.playerid, pl.nickname, " +
                    "pr.id as pr_id, pr.score as pr_score, pr.maxscore as pr_maxscore, pr.resourceid as pr_resource_id, " +
                    "i.id as i_id, i.count as i_count, i.level as i_level, i.resourceid as i_resource_id, " +
                    "c.id as c_id, c.resourceid as c_resource_id, c.count as c_count, c.name as c_name " +
                    "FROM players pl " +
                    "left join progresses pr on pl.playerid = pr.playerid " +
                    "left join player_item_map pim on pl.playerid = pim.playerid " +
                    "left join items i on pim.itemid = i.id " +
                    "left join player_currency_map pcm on pl.playerid = pcm.playerid " +
                    "left join currencies c on c.id = pcm.currencyid;");
            preparedStatement.execute();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return convertToPlayers(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM players WHERE playerId = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement( "SELECT EXISTS(SELECT * FROM players where playerid = ?);");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            return preparedStatement.getResultSet().getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private List<Player> convertToPlayers(ResultSet resultSet) throws SQLException {
        Map<Long, Player> playerMap = new HashMap<>();
        Map<Long, Currency> currencyMap = new HashMap<>();
        Map<Long, Item> itemMap = new HashMap<>();
        Map<Long, Progress> progressMap = new HashMap<>();
        while (resultSet.next()) {
            Long playerId = resultSet.getLong("playerId");
            String playerName = resultSet.getString("nickname");
            Long progressId = resultSet.getLong("pr_id");
            Integer progressScore = resultSet.getInt("pr_score");
            Integer progressMaxScore = resultSet.getInt("pr_maxscore");
            Long progressResourceId = resultSet.getLong("pr_resource_id");
            Long itemId = resultSet.getLong("i_id");
            Integer itemCount = resultSet.getInt("i_count");
            Integer itemLevel = resultSet.getInt("i_level");
            Long itemResourceId = resultSet.getLong("i_resource_id");
            Long currencyId = resultSet.getLong("c_id");
            Long currencyResourceId = resultSet.getLong("c_resource_id");
            Integer currencyCount = resultSet.getInt("c_count");
            String currencyName = resultSet.getString("c_name");
            if (playerId != 0) {
                if (!playerMap.containsKey(playerId)) {
                    playerMap.put(playerId, Player.builder()
                            .playerId(playerId)
                            .items(new HashMap<>())
                            .currencies(new HashMap<>())
                            .progresses(new ArrayList<>())
                            .nickname(playerName).build());
                }
            } else {
                continue;
            }
            if (progressId != 0) {
                if (!progressMap.containsKey(progressId)) {
                    Progress progress = Progress.builder()
                            .playerId(playerId)
                            .id(progressId)
                            .maxScore(progressMaxScore)
                            .score(progressScore)
                            .resourceId(progressResourceId)
                            .build();
                    playerMap.get(playerId).getProgresses().add(progress);
                    progressMap.put(progressId, progress);
                }
            }
            if (itemId != 0) {
                if (!itemMap.containsKey(itemId)) {
                    Item item = Item.builder()
                            .count(itemCount)
                            .id(itemId)
                            .resourceId(itemResourceId)
                            .level(itemLevel).build();
                    playerMap.get(playerId).getItems().put(itemId, item);
                    itemMap.put(itemId, item);
                }
            }
            if (currencyId != 0) {
                if (!currencyMap.containsKey(currencyId)) {
                    Currency currency = Currency
                            .builder()
                            .count(currencyCount)
                            .name(currencyName)
                            .id(currencyId)
                            .resourceId(currencyResourceId)
                            .build();
                    playerMap.get(playerId).getCurrencies().put(currencyId, currency);
                    currencyMap.put(currencyId, currency);
                }
            }
        }

        return playerMap.values().stream().toList();
    }

    private boolean existsInPlayerCurrencyMap(Connection connection, Long playerId, Long currencyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM player_currency_map where currencyid = ? and playerid = ?);");
        preparedStatement.setLong(1, currencyId);
        preparedStatement.setLong(2, playerId);
        preparedStatement.execute();
        preparedStatement.getResultSet().next();
        return preparedStatement.getResultSet().getBoolean(1);
    }

    private boolean existsInPlayerItemMap(Connection connection, Long playerId, Long itemId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM player_item_map where itemid = ? and playerid = ?);");
        preparedStatement.setLong(1, itemId);
        preparedStatement.setLong(2, playerId);
        preparedStatement.execute();
        preparedStatement.getResultSet().next();
        return preparedStatement.getResultSet().getBoolean(1);
    }

    private void insertIntoPlayerCurrencyMap(Connection connection, Long playerId, Long currencyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_currency_map(playerid, currencyid) values(?, ?);");
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, currencyId);
        preparedStatement.executeUpdate();
    }

    private void insertIntoPlayerItemMap(Connection connection, Long playerId, Long itemId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM player_item_map where itemid = ? and playerid = ?);");
        preparedStatement.setLong(1, playerId);
        preparedStatement.setLong(2, itemId);
        preparedStatement.executeUpdate();
    }
}
