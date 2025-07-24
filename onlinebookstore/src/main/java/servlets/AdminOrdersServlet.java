package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/adminorders")
public class AdminOrdersServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/onlinebookstore";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        out.println("<h2>All Orders</h2>");
        out.println("<table border='1' cellpadding='5' cellspacing='0'>");
        out.println("<thead><tr><th>Order ID</th><th>Username</th><th>Total Amount (RS)</th><th>Order Date</th><th>Items</th><th>Print Bill</th></tr></thead>");
        out.println("<tbody>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                String orderQuery = "SELECT order_id, username, total_amount, order_date FROM completed_orders ORDER BY order_date DESC";

                try (PreparedStatement psOrders = conn.prepareStatement(orderQuery);
                     ResultSet rsOrders = psOrders.executeQuery()) {

                    while (rsOrders.next()) {
                        int orderId = rsOrders.getInt("order_id");
                        String username = rsOrders.getString("username");
                        double total = rsOrders.getDouble("total_amount");
                        Timestamp date = rsOrders.getTimestamp("order_date");

                        // Fetch order items
                        String itemsQuery = "SELECT book_barcode, quantity, price FROM completed_order_items WHERE order_id = ?";
                        StringBuilder itemsList = new StringBuilder("<ul>");
                        try (PreparedStatement psItems = conn.prepareStatement(itemsQuery)) {
                            psItems.setInt(1, orderId);
                            try (ResultSet rsItems = psItems.executeQuery()) {
                                while (rsItems.next()) {
                                    String barcode = rsItems.getString("book_barcode");
                                    int qty = rsItems.getInt("quantity");
                                    double price = rsItems.getDouble("price");
                                    itemsList.append("<li>Book Barcode: ")
                                            .append(barcode)
                                            .append(" - Qty: ")
                                            .append(qty)
                                            .append(", Price: RS")
                                            .append(price)
                                            .append("</li>");
                                }
                            }
                        }
                        itemsList.append("</ul>");

                        out.printf(
                            "<tr>"
                            + "<td>%d</td>"
                            + "<td>%s</td>"
                            + "<td>RS%.2f</td>"
                            + "<td>%s</td>"
                            + "<td>%s</td>"
                            + "<td>"
                            + "<form action='printbill' method='get' style='margin:0;'>"
                            + "<input type='hidden' name='order_id' value='%d'/>"
                            + "<input type='submit' value='Print Bill'/>"
                            + "</form>"
                            + "</td>"
                            + "</tr>",
                            orderId,
                            username != null ? username : "N/A",
                            total,
                            date.toString(),
                            itemsList.toString(),
                            orderId
                        );
                    }
                }
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='6'>Error fetching orders.</td></tr>");
            e.printStackTrace(out);
        }

        out.println("</tbody></table>");
    }
}
