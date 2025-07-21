package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bittercode.constant.BookStoreConstants;
import com.bittercode.model.Cart;
import com.bittercode.model.UserRole;
import com.bittercode.util.StoreUtil;

public class CheckoutServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        PrintWriter pw = res.getWriter();
        res.setContentType(BookStoreConstants.CONTENT_TYPE_TEXT_HTML);

        // Check if the customer is logged in
        if (!StoreUtil.isLoggedIn(UserRole.CUSTOMER, req.getSession())) {
            RequestDispatcher rd = req.getRequestDispatcher("CustomerLogin.html");
            rd.include(req, res);
            pw.println("<table class=\"tab\"><tr><td>Please Login First to Continue!!</td></tr></table>");
            return;
        }

        HttpSession session = req.getSession();
        
        // Retrieve the cart from the session
        Object obj = session.getAttribute("cartItems");
        
        // Check if cartItems exists in the session
        if (obj == null) {
            pw.println("<h3>Your cart is empty or session expired. Please add items to cart first.</h3>");
            return;
        }

        List<Cart> cartItems;
        try {
            cartItems = (List<Cart>) obj;  // unchecked cast
        } catch (ClassCastException e) {
            pw.println("<h3>Error reading your cart. Please try again.</h3>");
            e.printStackTrace();
            return;
        }

        // If cart is empty
        if (cartItems.isEmpty()) {
            pw.println("<h3>Your cart is empty! Please add items to proceed.</h3>");
            return;
        }

        // Get the total amount to pay from session
        double amountToPay = 0;
        Object amountObj = session.getAttribute("amountToPay");
        if (amountObj != null && amountObj instanceof Double) {
            amountToPay = (Double) amountObj;
        } else {
            pw.println("<h3>Amount to pay is missing from the session. Please try again.</h3>");
            return;
        }

        // If amount is zero or invalid, reject the transaction
        if (amountToPay <= 0) {
            pw.println("<h3>Invalid amount to pay. Please try again.</h3>");
            return;
        }

        try {
            // Display the payment page if all checks pass
            RequestDispatcher rd = req.getRequestDispatcher("payment.html");
            if (rd != null) {
                rd.include(req, res);
            } else {
                pw.println("<h3>Payment page is not found. Please try again later.</h3>");
            }

            // Display total amount and proceed button
            pw.println("<h3>Total Amount<span class=\"price\" style=\"color: black\"><b>RS; " + amountToPay + "</b></span></h3>");
            pw.println("<form method=\"POST\" action=\"finalizePayment\"><input type=\"submit\" value=\"Pay & Place Order\" class=\"btn\"/></form>");
            
        } catch (Exception e) {
            pw.println("<h3>Error rendering payment page. Please try again later.</h3>");
            e.printStackTrace();
        }
    }
}
