package tn.esprit.utils;

import tn.esprit.entities.Command;
import tn.esprit.entities.Produit;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.List;
import java.util.Properties;

public class EmailSender {

    public static void sendInvoiceEmail(String toEmail, Command command, List<Produit> products) throws MessagingException {
        final String username = "tasnimnaili2012@gmail.com";
        final String password = "guuwstyqizgtxchw";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        String subject = "Invoice for Order #" + command.getId();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><style>")
                .append("body{font-family:Arial,sans-serif;margin:0;padding:0;background:#f5f5f5}")
                .append(".container{width:80%;max-width:800px;margin:40px auto;background:#fff;padding:20px;box-shadow:0 0 10px rgba(0,0,0,0.1)}")
                .append(".header{text-align:center;padding-bottom:20px;border-bottom:2px solid #2E86C1}")
                .append(".header h1{margin:0;color:#2E86C1}")
                .append(".invoice-details{margin:20px 0}")
                .append(".invoice-details table{width:100%}")
                .append(".invoice-details td{padding:5px}")
                .append(".products table{width:100%;border-collapse:collapse}")
                .append(".products th,.products td{border:1px solid #ddd;padding:10px;text-align:left}")
                .append(".products th{background:#2E86C1;color:#fff}")
                .append(".products tr:nth-child(even){background:#f9f9f9}")
                .append(".total-row td{border:none;padding-top:10px;font-weight:bold}")
                .append(".footer{text-align:center;margin-top:30px;font-size:12px;color:#777}")
                .append("</style></head><body>")
                .append("<div class='container'>")
                .append("<div class='header'><h1>Invoice</h1></div>")
                .append("<div class='invoice-details'><table>")
                .append("<tr><td><strong>Order #:</strong> ").append(command.getId()).append("</td>")
                .append("<td><strong>Date:</strong> ").append(command.getCreate_at()).append("</td></tr>")
                .append("<tr><td><strong>Status:</strong> ").append(command.getStatus()).append("</td>")
                .append("<td><strong>Total Amount:</strong> ").append(String.format("%.2f TND", command.getTotal_amount())).append("</td></tr>")
                .append("</table></div>")
                .append("<div class='products'><table>")
                .append("<tr><th>Product</th><th>Unit Price</th><th>Qty</th><th>Subtotal</th></tr>");

        double grandTotal = 0;
        for (Produit p : products) {
            int qty = 1;
            double subtotal = p.getPrix() * qty;
            grandTotal += subtotal;
            html.append("<tr>")
                    .append("<td>").append(p.getNom()).append("</td>")
                    .append("<td>").append(String.format("%.2f TND", p.getPrix())).append("</td>")
                    .append("<td>").append(qty).append("</td>")
                    .append("<td>").append(String.format("%.2f TND", subtotal)).append("</td>")
                    .append("</tr>");
        }

        html.append("<tr class='total-row'><td colspan='3' style='text-align:right'>Grand Total</td>")
                .append("<td>").append(String.format("%.2f TND", grandTotal)).append("</td></tr>")
                .append("</table></div>")
                .append("<div class='invoice-details'><strong>Delivery Address:</strong> ").append(command.getDelivery_address()).append("</div>")
                .append("<div class='invoice-details'><strong>Notes:</strong> ").append(command.getNotes()).append("</div>")
                .append("<div class='footer'>Thank you for your purchase!</div>")
                .append("</div></body></html>");

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(html.toString(), "text/html; charset=UTF-8");

        Transport.send(message);
    }
}
