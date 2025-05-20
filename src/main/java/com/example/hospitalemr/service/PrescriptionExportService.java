package com.example.hospitalemr.service;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.domain.Prescription;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PrescriptionExportService {

    private static final String TEMPLATE_PATH = "/templates/prescription_template.docx";

    public byte[] generatePrescriptionDoc(Patient patient, List<Prescription> prescriptions) throws Exception {
        InputStream is = getClass().getResourceAsStream(TEMPLATE_PATH);
        XWPFDocument document = new XWPFDocument(is);

        // 일반 텍스트 치환
        for (XWPFParagraph para : document.getParagraphs()) {
            replaceTextInParagraph(para, patient);
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceTextInParagraph(paragraph, patient);
                    }
                }
            }
        }

        // 테이블 데이터 채우기
        for (XWPFTable table : document.getTables()) {
            for (int i = 0; i < table.getNumberOfRows(); i++) {
                XWPFTableRow row = table.getRow(i);

                boolean isTemplateRow = row.getTableCells().stream().anyMatch(cell ->
                        cell.getText().contains("${drug_code}"));

                if (isTemplateRow) {
                    // 템플릿 행 복제 및 데이터 채우기
                    for (Prescription p : prescriptions) {
                        XWPFTableRow newRow = table.insertNewTableRow(i++); // insert & increment
                        for (int j = 0; j < row.getTableCells().size(); j++) {
                            XWPFTableCell newCell = newRow.addNewTableCell();
                            String replaced = row.getCell(j).getText()
                                    .replace("${drug_code}", p.getDrugCode())
                                    .replace("${drug_name}", p.getDrugName())
                                    .replace("${dosage}", p.getDosage())
                                    .replace("${days}", String.valueOf(p.getDays()))
                                    .replace("${frequency_per_day}", String.valueOf(p.getFrequencyPerDay()))
                                    .replace("${timing}", p.getTiming());
                            newCell.setText(replaced);
                        }
                    }

                    table.removeRow(i); // 마지막에 템플릿 행 삭제
                    break;
                }
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        document.close();
        return out.toByteArray();
    }

    private void replaceTextInParagraph(XWPFParagraph para, Patient patient) {
        for (XWPFRun run : para.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                text = text.replace("${name}", patient.getName())
                        .replace("${dob}", String.valueOf(patient.getDate_of_birth()))
                        .replace("${gender}", patient.getGender())
                        .replace("${contact}", patient.getPhone_number());
                run.setText(text, 0);
            }
        }
    }

    public File convertDocxToPdf(File docxFile) throws IOException, InterruptedException {
        String libreOfficePath = "soffice"; // "C:\\Program Files\\LibreOffice\\program\\soffice.exe"
        File pdfFile = new File(docxFile.getParent(), docxFile.getName().replace(".docx", ".pdf"));

        ProcessBuilder builder = new ProcessBuilder(
                libreOfficePath,
                "--headless",
                "--convert-to", "pdf",
                "--outdir", docxFile.getParent(),
                docxFile.getAbsolutePath()
        );
        Process process = builder.start();
        if (process.waitFor() != 0) {
            throw new IOException("LibreOffice PDF 변환 실패");
        }
        return pdfFile;
    }

}
