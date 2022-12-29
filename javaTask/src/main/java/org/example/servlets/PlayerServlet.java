package org.example.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.example.dao.PlayerDAO;
import org.example.pojo.Player;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class PlayerServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    private final PlayerDAO playerDAO;

    public PlayerServlet(ObjectMapper objectMapper, ObjectWriter objectWriter, PlayerDAO playerDAO) {
        this.objectMapper = objectMapper;
        this.objectWriter = objectWriter;
        this.playerDAO = playerDAO;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long playerId = Long.parseLong(request.getParameter("id"));
        Optional<Player> player = playerDAO.getById(playerId);
        if (player.isEmpty()) {
            response.setStatus(404);
        } else {
            PrintWriter printWriter = response.getWriter();
            printWriter.println(objectWriter.writeValueAsString(player.get()));
            printWriter.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String json = IOUtils.toString(req.getReader());
        Player player = objectMapper.readValue(json, Player.class);
        Optional<Player> playerOpt = playerDAO.getById(player.getPlayerId());
        if (playerOpt.isEmpty()) {
            resp.setStatus(404);
        } else {
            player = playerDAO.update(player);
            PrintWriter printWriter = resp.getWriter();
            printWriter.println(objectWriter.writeValueAsString(player));
            printWriter.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = IOUtils.toString(req.getReader());
        Player player = objectMapper.readValue(json, Player.class);
        Optional<Player> playerOpt = playerDAO.getById(player.getPlayerId());
        if (playerOpt.isEmpty()) {
            resp.setStatus(404);
        } else {
            playerDAO.delete(player.getPlayerId());
            resp.setStatus(200);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = IOUtils.toString(request.getReader());
        Player player = objectMapper.readValue(json, Player.class);
        player = playerDAO.update(player);
        PrintWriter printWriter = response.getWriter();
        printWriter.println(objectWriter.writeValueAsString(player));
        printWriter.close();
    }
}
