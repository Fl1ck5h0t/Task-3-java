package org.example.dao;

import org.example.config.ConnectionValues;
import org.example.pojo.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.example.config.ConnectionValues.*;

public class CurrencyDAO implements CrudDAO<Currency, Long> {

    @Override
    public Currency insert(Currency currency) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currencies (id, resourceid, name, count) VALUES  (?, ?, ?, ?);");
            preparedStatement.setLong(1, currency.getId());
            preparedStatement.setLong(2, currency.getResourceId());
            preparedStatement.setString(3, currency.getName());
            preparedStatement.setInt(4, currency.getCount());
            preparedStatement.executeUpdate();
            return currency;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Currency update(Currency currency) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (currency.getId() != null && existById(currency.getId())) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE currencies SET (resourceid, name, count) = (?, ?, ?) where id = ?;");
                preparedStatement.setLong(1, currency.getResourceId());
                preparedStatement.setString(2, currency.getName());
                preparedStatement.setInt(3, currency.getCount());
                preparedStatement.setLong(4, currency.getId());
                preparedStatement.executeUpdate();
                return currency;
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<Currency> getById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies where id = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            return Optional.ofNullable(createCurrency(preparedStatement.getResultSet()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Currency> getAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies;");
            preparedStatement.execute();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    currencies.add(createCurrency(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM currencies WHERE id = ?;");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existById(Long id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT EXISTS(SELECT * FROM currencies where id = ?);");
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            return preparedStatement.getResultSet().getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private Currency createCurrency(ResultSet resultSet) throws SQLException {
        Long resourceId = resultSet.getLong("resourceid");
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        int count = resultSet.getInt("count");
        return Currency.builder()
                .id(id)
                .resourceId(resourceId)
                .count(count)
                .name(name)
                .build();
    }
}
