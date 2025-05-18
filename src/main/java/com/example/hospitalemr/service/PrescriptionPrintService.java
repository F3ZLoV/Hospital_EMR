package com.example.hospitalemr.service;

import com.example.hospitalemr.domain.Patient;
import com.example.hospitalemr.domain.Prescription;
import jakarta.xml.bind.JAXBElement;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionPrintService {

    private static final String TEMPLATE_PATH = "/templates/prescription_template.docx";

    public ByteArrayOutputStream generatePrescriptionDocx(Patient patient, List<Prescription> prescriptions) throws Exception {
        // 템플릿 불러오기
        InputStream is = getClass().getResourceAsStream(TEMPLATE_PATH);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(is);
        MainDocumentPart mainPart = wordMLPackage.getMainDocumentPart();

        // 변수 치환
        Map<String, String> variables = Map.of(
                "${name}", patient.getName(),
                "${dob}", String.valueOf(patient.getDate_of_birth()),
                "${gender}", patient.getGender(),
                "${contact}", patient.getPhone_number()
        );
        mainPart.variableReplace(variables);

        // 테이블 데이터 채우기
        List<Object> tables = mainPart.getJAXBNodesViaXPath("//w:tbl", true);
        if (!tables.isEmpty()) {
            Tbl table = (Tbl) tables.get(0);
            Tr templateRow = (Tr) table.getContent().get(1);

            for (Prescription p : prescriptions) {
                Tr newRow = (Tr) XmlUtils.deepCopy(templateRow);
                List<Object> cells = newRow.getContent();

                if (cells.size() >= 6) {
                    setCellText((Tc) ((JAXBElement<?>) cells.get(0)).getValue(), p.getDrugCode());             // 약품 코드
                    setCellText((Tc) ((JAXBElement<?>) cells.get(1)).getValue(), p.getDrugName());             // 약품 명
                    setCellText((Tc) ((JAXBElement<?>) cells.get(2)).getValue(), p.getDosage());               // 용량
                    setCellText((Tc) ((JAXBElement<?>) cells.get(3)).getValue(), p.getDays().toString());      // 총 투약일수
                    setCellText((Tc) ((JAXBElement<?>) cells.get(4)).getValue(), p.getFrequencyPerDay().toString()); // 일일 투약 횟수
                    setCellText((Tc) ((JAXBElement<?>) cells.get(5)).getValue(), p.getTiming());               // 비고
                }

                table.getContent().add(newRow);
            }

            // 템플릿 row 제거
            table.getContent().remove(templateRow);
        }

        // Word 문서로 출력
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wordMLPackage.save(out);
        return out;
    }

    private void setCellText(Tc cell, String value) {
        cell.getContent().clear();
        P p = new P();
        R r = new R();
        Text t = new Text();
        t.setValue(value);
        r.getContent().add(t);
        p.getContent().add(r);
        cell.getContent().add(p);
    }
}
