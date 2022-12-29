package org.example.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.example.dao.ItemDAO;
import org.example.dao.ProgressDAO;
import org.example.pojo.Currency;
import org.example.pojo.Progress;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class ProgressServlet extends HttpServlet {
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    private final ProgressDAO progressDAO;

    public ProgressServlet(ObjectMapper objectMapper, ObjectWriter objectWriter, ProgressDAO progressDAO) {
        this.objectMapper = objectMapper;
        this.objectWriter = objectWriter;
        this.progressDAO = progressDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long progressId = Long.parseLong(request.getParameter("id"));
        Optional<Progress> progress = progressDAO.getById(progressId);
        if (progress.isEmpty()) {
            response.setStatus(404);
        } else {
            PrintWriter printWriter = response.getWriter();
            printWriter.println(objectWriter.writeValueAsString(progress.get()));
            printWriter.close();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        String json = IOUtils.toString(req.getReader());
        Progress progress = objectMapper.readValue(json, Progress.class);
        Optional<Progress> progressOptional = progressDAO.getById(progress.getId());
        if (progressOptional.isEmpty()) {
            resp.setStatus(404);
        } else {
            progress = progressDAO.update(progress);
            PrintWriter printWriter = resp.getWriter();
            printWriter.println(objectWriter.writeValueAsString(progress));
            printWriter.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String json = IOUtils.toString(request.getReader());
        Progress progress = objectMapper.readValue(json, Progress.class);
        progress = progressDAO.insert(progress);
        PrintWriter printWriter = response.getWriter();
        printWriter.println(objectWriter.writeValueAsString(progress));
        printWriter.close();
    }
}
