/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.medicalappointmentandprescription.utils;

import com.mycompany.medicalappointmentandprescription.models.Prescription;
import com.mycompany.medicalappointmentandprescription.models.Patient;
import com.mycompany.medicalappointmentandprescription.models.Doctor;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;

public class PDFExportUtil {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private PDFExportUtil() {
    }

    // Common save location selection
    private static File choosePDFLocation(String defaultFileName) {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Save PDF Report");

        chooser.setSelectedFile(
                new File(defaultFileName + ".pdf")
        );

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "PDF Documents (*.pdf)", "pdf"
                )
        );

        int result = chooser.showSaveDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getAbsolutePath() + ".pdf");
        }

        if (file.exists()) {

            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "File already exists. Replace it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return file;
    }

    // Common PDF title
    private static void addTitle(
            Document document, String title)
            throws DocumentException {

        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                18
        );

        Paragraph heading =
                new Paragraph(title, titleFont);

        heading.setAlignment(Element.ALIGN_CENTER);

        heading.setSpacingAfter(15);

        document.add(heading);
    }

    // Common section heading
    private static void addSectionHeading(
            Document document, String heading)
            throws DocumentException {

        Font font = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                12
        );

        Paragraph paragraph =
                new Paragraph(heading, font);

        paragraph.setSpacingBefore(10);
        paragraph.setSpacingAfter(8);

        document.add(paragraph);
    }

    // Add two-column details
    private static void addDetail(
            Document document,
            String label,
            String value) throws DocumentException {

        Paragraph paragraph = new Paragraph();

        Font boldFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD, 10
        );

        Font normalFont = FontFactory.getFont(
                FontFactory.HELVETICA, 10
        );

        paragraph.add(new Phrase(label + ": ", boldFont));

        paragraph.add(new Phrase(
                value == null ? "-" : value,
                normalFont
        ));

        document.add(paragraph);
    }

    // Export individual prescription for Staff
    public static boolean exportStaffPrescriptionPDF(
            Prescription prescription,
            Patient patient,
            Doctor doctor) {

        if (prescription == null
                || patient == null
                || doctor == null) {

            JOptionPane.showMessageDialog(
                    null,
                    "Prescription or related details not found."
            );

            return false;
        }

        File file = choosePDFLocation(
                "Prescription_" + prescription.getPrescriptionId()
        );

        if (file == null) {
            return false;
        }

        Document document = new Document();

        try {

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(file)
            );

            document.open();

            addTitle(
                    document,
                    "MEDICAL PRESCRIPTION"
            );

            addDetail(
                    document,
                    "Generated On",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy hh:mm a"
                            )
                    )
            );

            addSectionHeading(
                    document,
                    "PRESCRIPTION DETAILS"
            );

            addDetail(
                    document,
                    "Prescription ID",
                    String.valueOf(
                            prescription.getPrescriptionId()
                    )
            );

            addDetail(
                    document,
                    "Issue Date",
                    prescription.getIssueDate() == null
                            ? "-"
                            : prescription.getIssueDate()
                                    .format(DATE_FORMAT)
            );

            addSectionHeading(
                    document,
                    "PATIENT DETAILS"
            );

            addDetail(
                    document,
                    "Patient ID",
                    String.valueOf(patient.getPatientId())
            );

            addDetail(
                    document,
                    "Patient Name",
                    patient.getFullName()
            );

            addSectionHeading(
                    document,
                    "DOCTOR DETAILS"
            );

            addDetail(
                    document,
                    "Doctor ID",
                    String.valueOf(doctor.getDoctorId())
            );

            addDetail(
                    document,
                    "Doctor Name",
                    doctor.getDoctorName()
            );

            addDetail(
                    document,
                    "Specialization",
                    doctor.getSpecialization()
            );

            addSectionHeading(
                    document,
                    "MEDICINE DETAILS"
            );

            addDetail(
                    document,
                    "Medicine and Dosage",
                    prescription.getMedicineAndDosage()
            );

            document.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Prescription PDF exported successfully."
            );

            return true;

        } catch (DocumentException | IOException e) {

            if (document.isOpen()) {
                document.close();
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Unable to export PDF: " + e.getMessage()
            );

            return false;
        }
    }

    // Export Doctor's prescription report
    public static boolean exportDoctorReportPDF(
            Doctor doctor,
            List<Prescription> prescriptions,
            List<Patient> patients) {

        if (doctor == null || prescriptions == null
                || patients == null) {

            JOptionPane.showMessageDialog(
                    null,
                    "Required report data is missing."
            );

            return false;
        }

        File file = choosePDFLocation(
                "Doctor_Prescription_Report"
        );

        if (file == null) {
            return false;
        }

        Document document = new Document();

        try {

            PdfWriter.getInstance(
                    document,
                    new FileOutputStream(file)
            );

            document.open();

            addTitle(
                    document,
                    "DOCTOR PRESCRIPTION REPORT"
            );

            addDetail(
                    document,
                    "Doctor ID",
                    String.valueOf(doctor.getDoctorId())
            );

            addDetail(
                    document,
                    "Doctor Name",
                    doctor.getDoctorName()
            );

            addDetail(
                    document,
                    "Specialization",
                    doctor.getSpecialization()
            );

            addDetail(
                    document,
                    "Generated On",
                    LocalDate.now().format(DATE_FORMAT)
            );

            addSectionHeading(
                    document,
                    "PRESCRIPTION RECORDS"
            );

            PdfPTable table = new PdfPTable(4);

            table.setWidthPercentage(100);

            table.setWidths(new float[]{1.2f, 2f, 2f, 3f});

            addTableHeader(table, "Prescription ID");
            addTableHeader(table, "Patient Name");
            addTableHeader(table, "Issue Date");
            addTableHeader(table, "Medicine and Dosage");

            int recordCount = 0;

            for (Prescription prescription : prescriptions) {

                // Ensure only this doctor's prescriptions are included
                if (prescription.getDoctorId()
                        != doctor.getDoctorId()) {
                    continue;
                }

                Patient patient = null;

                for (Patient p : patients) {

                    if (p.getPatientId()
                            == prescription.getPatientId()) {

                        patient = p;
                        break;
                    }
                }

                if (patient == null) {
                    continue;
                }

                table.addCell(
                        String.valueOf(
                                prescription.getPrescriptionId()
                        )
                );

                table.addCell(patient.getFullName());

                table.addCell(
                        prescription.getIssueDate() == null
                                ? "-"
                                : prescription.getIssueDate()
                                        .format(DATE_FORMAT)
                );

                table.addCell(
                        prescription.getMedicineAndDosage()
                );

                recordCount++;
            }

            if (recordCount == 0) {

                document.add(
                        new Paragraph(
                                "No prescription records found."
                        )
                );

            } else {

                document.add(table);
            }

            document.add(new Paragraph(
                    "\nTotal Prescriptions: " + recordCount
            ));

            document.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Doctor report exported successfully."
            );

            return true;

        } catch (DocumentException | IOException e) {

            if (document.isOpen()) {
                document.close();
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Unable to export PDF: " + e.getMessage()
            );

            return false;
        }
    }

    // Table header helper
    private static void addTableHeader(
            PdfPTable table, String heading) {

        Font font = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                9
        );

        PdfPCell cell = new PdfPCell(
                new Phrase(heading, font)
        );

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);

        table.addCell(cell);
    }
}
