package org.example.dao;

import org.example.pojo.Currency;
import org.example.pojo.Item;
import org.example.pojo.Progress;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.example.config.ConnectionValues.*;

public class ProgressDAO implements CrudDAO<Progress, Long> {
    @Override
    public Progress insert(Progress progress) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO progresses (id, playerid, resourceid, score, maxscore) VALUES (?, ?, ?, ?, ?);");
            preparedStatement.setLong(1, progress.getId());
            preparedStatement.setLong(2, progress.getPlayerId());
            preparedStatement.setLong(3, progress.getResourceId());
            preparedStatement.setInt(4, progress.getScore());
            preparedStatement.setInt(5, progress.getMaxScore());
            preparedStatement.executeUpdate();
            return progress;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Progress update(Progress progress) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (progress.getId() != null && existById(progress.getId())) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE progresses SET (playerid, resourceid, score, maxscore) = (?, ?, ?, ?) where id = ?;");
                preparedStatement.setLong(1, progress.getPlayerId());
                preparedStatement.setLong(2, progress.getResourceId());
                preparedStatement.setInt(3, progress.getScore());
                preparedStatement.setInt(4, progress.getMaxScore());
                preparedStatement.executeUpdate();
                return progress;
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<Progress> getById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM progresses where id = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return Optional.ofNullable(createProgress(preparedStatement.getResultSet()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Progress> getAll() {
        List<Progress> progresses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM progresses;");
            preparedStatement.execute();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    progresses.add(createProgress(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progresses;
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM progresses WHERE id = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM progresses where id = ?);");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            return preparedStatement.getResultSet().getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Progress createProgress(ResultSet resultSet) throws SQLException {
        Long resourceId = resultSet.getLong("resourceid");
        Long id = resultSet.getLong("id");
        int score = resultSet.getInt("score");
        int maxscore = resultSet.getInt("maxscore");
        Long playerId = resultSet.getLong("playerid");
        return Progress
                .builder()
                .id(id)
                .playerId(playerId)
                .resourceId(resourceId)
                .score(score)
                .maxScore(maxscore)
                .build();
    }
}
