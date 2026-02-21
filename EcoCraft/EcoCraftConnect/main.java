import com.sun.net.httpserver.*;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

public class Main {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/ecocraft_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "sophiapoppy@1310"; // put your MySQL password

    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // ===== STATIC FILES =====
        server.createContext("/", exchange -> {

            if (exchange.getRequestURI().getPath().startsWith("/api")) return;

            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            File file = new File("public" + path);

            if (file.exists()) {
                byte[] response = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type",
                        Files.probeContentType(file.toPath()));
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
            exchange.close();
        });

        // ===== GET PRODUCTS (HOME + SEARCH) =====
        server.createContext("/api/products", exchange -> {

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
                String search = params.getOrDefault("search", "");

                PreparedStatement stmt;

                if (search.isEmpty()) {
                    stmt = conn.prepareStatement("SELECT * FROM materials");
                } else {
                    stmt = conn.prepareStatement(
                            "SELECT * FROM materials WHERE LOWER(name) LIKE LOWER(?)");
                    stmt.setString(1, "%" + search + "%");
                }

                ResultSet rs = stmt.executeQuery();
                List<Map<String, Object>> list = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("name", rs.getString("name"));
                    p.put("price", rs.getDouble("price"));
                    p.put("type", rs.getString("type"));
                    p.put("img", rs.getString("image_url"));
                    list.add(p);
                }

                String json = gson.toJson(list);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.length());
                exchange.getResponseBody().write(json.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        });

        // ===== SELL PRODUCT =====
        server.createContext("/api/sell", exchange -> {

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                InputStreamReader reader =
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Map<String, Object> data = gson.fromJson(reader, Map.class);

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO materials (name, category, price, type, image_url) VALUES (?, ?, ?, ?, ?)");

                ps.setString(1, data.get("name").toString());
                ps.setString(2, data.get("category").toString());
                ps.setDouble(3, Double.parseDouble(data.get("price").toString()));
                ps.setString(4, data.get("type").toString());
                ps.setString(5, data.get("img").toString());

                ps.executeUpdate();

                exchange.sendResponseHeaders(201, -1);

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        });

        // ===== ORDER PRODUCT =====
        server.createContext("/api/order", exchange -> {

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                InputStreamReader reader =
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Map<String, Object> data = gson.fromJson(reader, Map.class);

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO orders (product_name, price, status) VALUES (?, ?, ?)");

                ps.setString(1, data.get("name").toString());
                ps.setDouble(2, Double.parseDouble(data.get("price").toString()));
                ps.setString(3, "Ordered");

                ps.executeUpdate();

                exchange.sendResponseHeaders(201, -1);

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        });

        // ===== GET ORDERS =====
        server.createContext("/api/orders", exchange -> {

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM orders");
                List<Map<String, Object>> list = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> o = new HashMap<>();
                    o.put("product", rs.getString("product_name"));
                    o.put("price", rs.getDouble("price"));
                    o.put("status", rs.getString("status"));
                    list.add(o);
                }

                String json = gson.toJson(list);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.length());
                exchange.getResponseBody().write(json.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        });

        // ===== LOGIN =====
        server.createContext("/api/login", exchange -> {

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                InputStreamReader reader =
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                Map<String, Object> data = gson.fromJson(reader, Map.class);

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=?");

                ps.setString(1, data.get("username").toString());
                ps.setString(2, data.get("password").toString());

                ResultSet rs = ps.executeQuery();

                if (rs.next())
                    exchange.sendResponseHeaders(200, -1);
                else
                    exchange.sendResponseHeaders(401, -1);

            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
            exchange.close();
        });

        server.start();
        System.out.println("AmazonEco running at http://localhost:8000");
    }

    private static Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2)
                result.put(URLDecoder.decode(pair[0], "UTF-8"),
                           URLDecoder.decode(pair[1], "UTF-8"));
        }
        return result;
    }
}