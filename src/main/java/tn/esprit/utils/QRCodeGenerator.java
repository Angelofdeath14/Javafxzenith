package tn.esprit.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import tn.esprit.entities.Produit;

public class QRCodeGenerator {

    public static Image generateQRCodeImage(String data, int width, int height) throws Exception {
        BitMatrix matrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
        WritableImage image = new WritableImage(width, height);
        PixelWriter pw = image.getPixelWriter();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pw.setColor(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return image;
    }

    public static String formatProductData(Produit p) {
        return "ID:" + p.getId()
                + ";Name:" + p.getNom()
                + ";Category:" + p.getCategorie()
                + ";Price:" + p.getPrix()
                + ";Description:" + p.getDescription();
    }
}
