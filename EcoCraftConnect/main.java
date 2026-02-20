import com.sun.net.httpserver.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecocraft_db";
    private static final String DB_USER = "root"; 
    private static final String DB_PASS = "password"; // Change to your MySQL password

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Serve Static Files (HTML, CSS, JS, Images)
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            File file = new File("public" + path);
            if (file.exists()) {
                byte[] response = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } else {
                exchange.sendResponseHeaders(404, 0);
            }
            exchange.getResponseBody().close();
        });

        // API Endpoint: Get and Search Products
        server.createContext("/api/products", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String searchTerm = "";
            if (query != null && query.contains("search=")) {
                searchTerm = query.split("=")[1].toLowerCase();
            }

            List<Map<String, Object>> products = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "SELECT * FROM materials WHERE LOWER(name) LIKE ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, "%" + searchTerm + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    products.add(Map.of(
                        "name", rs.getString("name"),
                        "category", rs.getString("category"),
                        "price", rs.getDouble("price"),
                        "img", rs.getString("image_url")
                    ));
                }
                String json = new Gson().toJson(products);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.length());
                exchange.getResponseBody().write(json.getBytes());
            } catch (Exception e) { e.printStackTrace(); }
            exchange.getResponseBody().close();
        });

        // API Endpoint: Seller Upload
        server.createContext("/api/sell", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                Map<String, Object> data = new Gson().fromJson(reader, Map.class);
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO materials (name, category, price, image_url) VALUES (?, ?, ?, ?)");
                    ps.setString(1, (String) data.get("name"));
                    ps.setString(2, (String) data.get("category"));
                    ps.setDouble(3, Double.parseDouble(data.get("price").toString()));
                    ps.setString(4, (String) data.get("img"));
                    ps.executeUpdate();
                    exchange.sendResponseHeaders(201, 0);
                } catch (Exception e) { e.printStackTrace(); }
            }
            exchange.getResponseBody().close();
        });

        System.out.println("Amazon-Eco running at http://localhost:8000");
        server.start();
    }
}