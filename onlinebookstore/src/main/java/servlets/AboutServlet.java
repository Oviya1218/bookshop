package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.model.UserRole;
import com.bittercode.util.StoreUtil;

public class AboutServlet extends HttpServlet {

    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");

        // If the store is logged in as customer or seller show about info
        if (StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
            RequestDispatcher rd = req.getRequestDispatcher("CustomerHome.html");
            rd.include(req, res);
            StoreUtil.setActiveTab(pw, "about");

            // Add About Us content for Customer
            pw.println("<div class='container'>");
            pw.println("<h2>About Us</h2>");
            pw.println("<p>Welcome to Pahana Edu Book Store. We are a leading bookshop in Colombo City. "
                    + "We are a team of passionate book lovers dedicated to providing the best selection of books for readers of all ages. "
                    + "Whether you're looking for the latest bestsellers, rare collections, or timeless classics, we have something for everyone.</p>");
            pw.println("<p>Our mission is to make reading accessible and enjoyable for all, and we're constantly adding new titles to our store. "
                    + "Our team works hard to ensure that every customer has a great shopping experience, whether you're browsing for a new book or reordering your favorite one.</p>");
            pw.println("<p><strong>Address:</strong> No.5, 1st Street, Colombo 05.</p>");
            pw.println("<p><strong>Hotline:</strong> 0112345678 / 0771234567</p>");
            pw.println("<p><strong>Email:</strong> Pahanaedu@gmail.com</p>");
            pw.println("<h3>Open Hours</h3>");
            pw.println("<p><strong>Mon - Sat:</strong> 8:00 AM to 5:00 PM</p>");
            pw.println("<p><strong>Sun:</strong> 8:00 AM to 1:00 PM</p>");
            pw.println("</div>");

        } else if (StoreUtil.isLoggedIn(UserRole.SELLER, req.getSession())) {
            RequestDispatcher rd = req.getRequestDispatcher("SellerHome.html");
            rd.include(req, res);
            StoreUtil.setActiveTab(pw, "about");

            // Add About Us content for Seller
            pw.println("<div class='container'>");
            pw.println("<h2>About Us</h2>");
            pw.println("<p>Welcome to Pahana Edu Book Store. We are a leading bookshop in Colombo City. "
                    + "We are a team of passionate book lovers dedicated to providing the best selection of books for readers of all ages. "
                    + "Whether you're looking for the latest bestsellers, rare collections, or timeless classics, we have something for everyone.</p>");
            pw.println("<p>Our mission is to make reading accessible and enjoyable for all, and we're constantly adding new titles to our store. "
                    + "Our team works hard to ensure that every customer has a great shopping experience, whether you're browsing for a new book or reordering your favorite one.</p>");
            pw.println("<p><strong>Address:</strong> No.5, 1st Street, Colombo 05.</p>");
            pw.println("<p><strong>Hotline:</strong> 0112345678 / 0771234567</p>");
            pw.println("<p><strong>Email:</strong> Pahanaedu@gmail.com</p>");
            pw.println("<h3>Open Hours</h3>");
            pw.println("<p><strong>Mon - Sat:</strong> 8:00 AM to 5:00 PM</p>");
            pw.println("<p><strong>Sun:</strong> 8:00 AM to 1:00 PM</p>");
            pw.println("</div>");

        } else {
            // If user is not logged in
            RequestDispatcher rd = req.getRequestDispatcher("login.html");
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
        }
    }
}

