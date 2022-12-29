package org.example.dao;

import org.example.pojo.Currency;
import org.example.pojo.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.example.config.ConnectionValues.*;

public class ItemDAO implements CrudDAO<Item, Long> {

    @Override
    public Item insert(Item item) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO items (id, count, level, resourceid) VALUES (?, ?, ?, ?);");
            preparedStatement.setLong(1, item.getId());
            preparedStatement.setInt(2, item.getCount());
            preparedStatement.setInt(3, item.getLevel());
            preparedStatement.setLong(4, item.getResourceId());
            preparedStatement.executeUpdate();
            return item;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Item update(Item item) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (item.getId() != null && existById(item.getId())) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE items SET (count, level, resourceid) = (?, ?, ?) where id = ?;");
                preparedStatement.setInt(1, item.getCount());
                preparedStatement.setInt(2, item.getLevel());
                preparedStatement.setLong(3, item.getResourceId());
                preparedStatement.executeUpdate();
                return item;
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Item> getById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM items where id = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return Optional.ofNullable(createItem(preparedStatement.getResultSet()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getAll() {
        List<Item> items = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM items;");
            preparedStatement.execute();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(createItem(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM items WHERE id = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM items where id = ?);");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            return preparedStatement.getResultSet().getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Item createItem(ResultSet resultSet) throws SQLException {
        Long resourceId = resultSet.getLong("resourceid");
        Long id = resultSet.getLong("id");
        int level = resultSet.getInt("level");
        int count = resultSet.getInt("count");
        return Item.builder()
                .id(id)
                .resourceId(resourceId)
                .level(level)
                .count(count)
                .build();
    }
}
