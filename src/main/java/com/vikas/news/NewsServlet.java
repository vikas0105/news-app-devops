package com.vikas.news;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.Properties;

public class NewsServlet extends HttpServlet {

    private String apiKey;
    private String baseUrl;

    @Override
    public void init() {
        try (InputStream input = getServletContext().getResourceAsStream("/WEB-INF/classes/config.properties")) {
            Properties props = new Properties();
            props.load(input);
            apiKey = props.getProperty("NEWS_API_KEY");
            baseUrl = props.getProperty("NEWS_URL");
        } catch (Exception e) {
            apiKey = "";
            baseUrl = "";
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String urlString = baseUrl + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) json.append(line);

        JSONObject data = new JSONObject(json.toString());
        String headline = data.getJSONArray("articles").getJSONObject(0).getString("title");

        req.setAttribute("headline", headline);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
