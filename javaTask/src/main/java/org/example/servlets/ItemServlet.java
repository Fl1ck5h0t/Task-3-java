package org.example.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.example.dao.ItemDAO;
import org.example.pojo.Item;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class ItemServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    private final ItemDAO itemDAO;

    public ItemServlet(ObjectMapper objectMapper, ObjectWriter objectWriter, ItemDAO itemDAO) {
        this.objectMapper = objectMapper;
        this.objectWriter = objectWriter;
        this.itemDAO = itemDAO;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long itemId = Long.parseLong(request.getParameter("id"));
        Optional<Item> item = itemDAO.getById(itemId);
        if (item.isEmpty()) {
            response.setStatus(404);
        } else {
            PrintWriter printWriter = response.getWriter();
            printWriter.println(objectWriter.writeValueAsString(item.get()));
            printWriter.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String json = IOUtils.toString(req.getReader());
        Item item = objectMapper.readValue(json, Item.class);
        Optional<Item> itemOptional = itemDAO.getById(item.getId());
        resp.setContentType("application/json");
        if (itemOptional.isEmpty()) {
            resp.setStatus(404);
        } else {
            item = itemDAO.update(item);
            PrintWriter printWriter = resp.getWriter();
            printWriter.println(objectWriter.writeValueAsString(item));
            printWriter.close();
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = IOUtils.toString(request.getReader());
        Item item = objectMapper.readValue(json, Item.class);
        item = itemDAO.insert(item);
        PrintWriter printWriter = response.getWriter();
        printWriter.println(objectWriter.writeValueAsString(item));
        printWriter.close();

    }
}
