package org.example.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.example.dao.CurrencyDAO;
import org.example.pojo.Currency;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    private final CurrencyDAO currencyDAO;

    public CurrencyServlet(ObjectMapper objectMapper, ObjectWriter objectWriter,CurrencyDAO currencyDAO) {
        this.objectMapper = objectMapper;
        this.objectWriter = objectWriter;
        this.currencyDAO = currencyDAO;
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long currencyId = Long.parseLong(request.getParameter("id"));
        Optional<Currency> currency = currencyDAO.getById(currencyId);
        if (currency.isEmpty()) {
            response.setStatus(404);
        } else {
            PrintWriter printWriter = response.getWriter();
            printWriter.println(objectWriter.writeValueAsString(currency.get()));
            printWriter.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        String json = IOUtils.toString(req.getReader());
        Currency currency = objectMapper.readValue(json, Currency.class);
        Optional<Currency> currencyOptional = currencyDAO.getById(currency.getId());
        if (currencyOptional.isEmpty()) {
            resp.setStatus(404);
        } else {
            currency = currencyDAO.update(currency);
            PrintWriter printWriter = resp.getWriter();
            printWriter.println(objectWriter.writeValueAsString(currency));
            printWriter.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = IOUtils.toString(request.getReader());
        Currency currency = objectMapper.readValue(json, Currency.class);
        currency = currencyDAO.insert(currency);
        PrintWriter printWriter = response.getWriter();
        printWriter.println(objectWriter.writeValueAsString(currency));
        printWriter.close();
    }
}
