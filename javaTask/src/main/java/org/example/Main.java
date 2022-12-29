package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.dao.CurrencyDAO;
import org.example.dao.ItemDAO;
import org.example.dao.PlayerDAO;
import org.example.dao.ProgressDAO;
import org.example.service.PlayerService;
import org.example.servlets.CurrencyServlet;
import org.example.servlets.ItemServlet;
import org.example.servlets.PlayerServlet;
import org.example.servlets.ProgressServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        ItemDAO itemDAO = new ItemDAO();
        ProgressDAO progressDAO = new ProgressDAO();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        PlayerDAO playerDAO = new PlayerDAO(progressDAO, itemDAO, currencyDAO);
        PlayerService playerService = new PlayerService(playerDAO);
        if (args.length > 0 && args[0].equals("console")) {
            ConsoleService consoleService = new ConsoleService(playerDAO);
            consoleService.readConsole();
        } else {
            Server server = new Server();
            int port = 8000;
            HttpConfiguration httpConfig = new HttpConfiguration();
            HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfig);
            ServerConnector serverConnector = new ServerConnector(server, httpConnectionFactory);
            serverConnector.setHost("localhost");
            serverConnector.setPort(port);
            server.setConnectors(new Connector[]{serverConnector});
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            context.setContextPath("/");
            context.addServlet(new ServletHolder(new PlayerServlet(objectMapper, objectWriter, playerDAO)), "/players/*");
            context.addServlet(new ServletHolder(new ItemServlet(objectMapper, objectWriter, itemDAO)), "/items/*");
            context.addServlet(new ServletHolder(new CurrencyServlet(objectMapper, objectWriter, currencyDAO)), "/progress/*");
            context.addServlet(new ServletHolder(new ProgressServlet(objectMapper, objectWriter, progressDAO)), "/currency/*");
            server.setHandler(context);
            server.start();
        }
    }
}