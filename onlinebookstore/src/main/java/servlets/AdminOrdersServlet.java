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

    // Show orders with status dropdown form
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        out.println("<h2>All Orders</h2>");
        out.println("<table border='1' cellpadding='5' cellspacing='0'>");
        out.println("<thead><tr><th>Order ID</th><th>Username</th><th>Total Amount (RS)</th><th>Order Date</th><th>Items</th><th>Status</th><th>Update Status</th><th>Print Bill</th></tr></thead>");
        out.println("<tbody>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                String orderQuery = "SELECT order_id, username, total_amount, order_date, status FROM completed_orders ORDER BY order_date DESC";

                try (PreparedStatement psOrders = conn.prepareStatement(orderQuery);
                     ResultSet rsOrders = psOrders.executeQuery()) {

                    while (rsOrders.next()) {
                        int orderId = rsOrders.getInt("order_id");
                        String username = rsOrders.getString("username");
                        double total = rsOrders.getDouble("total_amount");
                        Timestamp date = rsOrders.getTimestamp("order_date");
                        String status = rsOrders.getString("status");
                        if (status == null) status = "pending";

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
                            + "<td>%s</td>"
                            + "<td>"
                            + "<form action='adminorders' method='post' style='margin:0;'>"
                            + "<input type='hidden' name='order_id' value='%d'/>"
                            + "<select name='status'>"
                            + statusOption("pending", status)
                            + statusOption("confirmed", status)
                            + statusOption("shipped", status)
                            + statusOption("cancelled", status)
                            + "</select>"
                            + "<input type='submit' value='Update'/>"
                            + "</form>"
                            + "</td>"
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
                            status,
                            orderId,
                            orderId
                        );
                    }
                }
            }
        } catch (Exception e) {
            out.println("<tr><td colspan='8'>Error fetching orders.</td></tr>");
            e.printStackTrace(out);
        }

        out.println("</tbody></table>");
    }

    // Handle status update when admin submits the form
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String status = req.getParameter("status");
        String orderIdStr = req.getParameter("order_id");

        if (status == null || orderIdStr == null) {
            res.sendRedirect("adminorders");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(orderIdStr);
        } catch (NumberFormatException e) {
            res.sendRedirect("adminorders");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String updateSql = "UPDATE completed_orders SET status = ? WHERE order_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, status);
                    ps.setInt(2, orderId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Order status updated successfully for order ID " + orderId);
                    } else {
                        System.out.println("No order found with ID " + orderId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirect back to GET to refresh the list
        res.sendRedirect("adminorders");
    }

    // Helper method to create dropdown option with selected attribute
    private String statusOption(String optionValue, String currentStatus) {
        if (optionValue.equalsIgnoreCase(currentStatus)) {
            return "<option value='" + optionValue + "' selected>" + capitalize(optionValue) + "</option>";
        } else {
            return "<option value='" + optionValue + "'>" + capitalize(optionValue) + "</option>";
        }
    }

    // Capitalize first letter for display
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

