package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.Book;
import com.bittercode.model.Cart;
import com.bittercode.model.UserRole;
import com.bittercode.service.BookService;
import com.bittercode.service.impl.BookServiceImpl;
import com.bittercode.util.StoreUtil;

public class ProcessPaymentServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/onlinebookstore";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    BookService bookService = new BookServiceImpl();

    @SuppressWarnings("unchecked")
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
            RequestDispatcher rd = req.getRequestDispatcher("CustomerLogin.html");
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
            return;
        }

        HttpSession session = req.getSession();
        List<Cart> cartItems = null;
        if (session.getAttribute("cartItems") != null)
            cartItems = (List<Cart>) session.getAttribute("cartItems");

        String username = (String) session.getAttribute("username");
        Double amountToPay = (Double) session.getAttribute("amountToPay");

        if (cartItems == null || cartItems.isEmpty() || username == null || amountToPay == null) {
            pw.println("<h3>Your cart is empty or session expired.</h3>");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false); // start transaction

            // 1. Insert order into completed_orders
            String insertOrderSql = "INSERT INTO completed_orders (username, total_amount) VALUES (?, ?)";
            int orderId = -1;

            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, username);
                psOrder.setDouble(2, amountToPay);
                psOrder.executeUpdate();

                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // 2. Insert each cart item into completed_order_items
            String insertItemSql = "INSERT INTO completed_order_items (order_id, book_barcode, quantity, price) VALUES (?, ?, ?, ?)";

            try (PreparedStatement psItem = conn.prepareStatement(insertItemSql)) {
                for (Cart cart : cartItems) {
                    Book book = cart.getBook();

                    // Update book quantity
                    int availableQty = book.getQuantity() - cart.getQuantity();
                    bookService.updateBookQtyById(book.getBarcode(), availableQty);

                    psItem.setInt(1, orderId);
                    psItem.setString(2, book.getBarcode());
                    psItem.setInt(3, cart.getQuantity());
                    psItem.setDouble(4, book.getPrice());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit(); // commit transaction

            // Show confirmation page
            RequestDispatcher rd = req.getRequestDispatcher("CustomerHome.html");
            rd.include(req, res);
            StoreUtil.setActiveTab(pw, "cart");
            pw.println("<div id='topmid' style='background-color:grey'>Your Order has been placed successfully!</div>");
            pw.println("<div class=\"container\"><div class=\"card-columns\">");

            for (Cart cart : cartItems) {
                Book book = cart.getBook();
                pw.println(this.addBookToCard(book.getBarcode(), book.getName(), book.getAuthor(), book.getPrice(), book.getQuantity()));
                session.removeAttribute("qty_" + book.getBarcode());
            }

            pw.println("</div></div>");

            // Clear cart and session data
            session.removeAttribute("amountToPay");
            session.removeAttribute("cartItems");
            session.removeAttribute("items");
            session.removeAttribute("selectedBookId");

        } catch (Exception e) {
            e.printStackTrace();
            pw.println("<h3>Error processing your order. Please try again.</h3>");
        }
    }

    public String addBookToCard(String bCode, String bName, String bAuthor, double bPrice, int bQty) {
        String button = "<a href=\"#\" class=\"btn btn-info\">Order Placed</a>\r\n";
        return "<div class=\"card\">\r\n"
                + "                <div class=\"row card-body\">\r\n"
                + "                    <img class=\"col-sm-6\" src=\"logo.png\" alt=\"Card image cap\">\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <h5 class=\"card-title text-success\">" + bName + "</h5>\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        Author: <span class=\"text-primary\" style=\"font-weight:bold;\"> " + bAuthor
                + "</span><br>\r\n"
                + "                        </p>\r\n"
                + "                        \r\n"
                + "                    </div>\r\n"
                + "                </div>\r\n"
                + "                <div class=\"row card-body\">\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        <span style='color:blue;'>Order Id: ORD" + bCode + "TM </span>\r\n"
                + "                        <br><span class=\"text-danger\">Item Yet to be Delivered</span>\r\n"
                + "                        </p>\r\n"
                + "                    </div>\r\n"
                + "                    <div class=\"col-sm-6\">\r\n"
                + "                        <p class=\"card-text\">\r\n"
                + "                        Amount Paid: <span style=\"font-weight:bold; color:green\"> RS; " + bPrice
                + " </span>\r\n"
                + "                        </p>\r\n"
                + button
                + "                    </div>\r\n"
                + "                </div>\r\n"
                + "            </div>";
    }
}

