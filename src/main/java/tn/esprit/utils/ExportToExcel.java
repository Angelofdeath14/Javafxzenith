package tn.esprit.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tn.esprit.entity.Reclamation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
public class ExportToExcel{
    public static void exportToExcel(List<Reclamation> reclamations, File file) throws Exception{
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sh = wb.createSheet("Réclamations");
        int rowIdx = 0;
        Row header = sh.createRow(rowIdx++);
        String[] cols = {"ID","Titre","Description","Date création","ID utilisateur"};
        for(int i=0;i<cols.length;i++){ Cell c = header.createCell(i); c.setCellValue(cols[i]); }
        for(Reclamation r: reclamations){
            Row row = sh.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getTitre());
            row.createCell(2).setCellValue(r.getDescription());
            row.createCell(3).setCellValue(r.getDate_creation().toString());
            row.createCell(4).setCellValue(r.getId_user());
        }
        try(FileOutputStream out = new FileOutputStream(file)){
            wb.write(out);
        }
        wb.close();
    }
}
