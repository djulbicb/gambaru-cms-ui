package com.example.gambarucmsui.ui.panel;


import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeUpdatePort;
import com.example.gambarucmsui.ports.interfaces.subscription.AddSubscriptionPort;
import com.example.gambarucmsui.ports.interfaces.team.*;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ports.interfaces.user.UserUpdatePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.alert.AlertShowAttendanceController;
import com.example.gambarucmsui.ui.alert.AlertShowMembershipController;
import com.example.gambarucmsui.ui.dto.admin.SubscriptStatus;
import com.example.gambarucmsui.ui.dto.admin.TeamDetail;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.util.FormatUtil;
import com.example.gambarucmsui.util.generators.BarcodeGenerator;
import com.example.gambarucmsui.util.generators.BarcodeView;
import com.example.gambarucmsui.util.generators.PDFGenerator;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.GREEN_CHECKMARK;
import static com.example.gambarucmsui.ui.dto.admin.SubscriptStatus.ORANGE_EXCLAMATION;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;
import static com.example.gambarucmsui.util.LayoutUtil.*;
import static com.example.gambarucmsui.util.PathUtil.*;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;


public class PanelAdminTeamController implements PanelHeader {
    @FXML
    private TableView<TeamDetail> tableTeam;
    @FXML
    private TableView<UserDetail> tableUser;

    private final Stage primaryStage;
    private final UserSavePort userSavePort;
    private final TeamDeletePort teamDeletePort;
    private final UserUpdatePort userUpdatePort;
    private final AddSubscriptionPort addSubscriptionPort;
    private final TeamSavePort teamSavePort;
    private final TeamLoadPort teamLoadPort;
    private final UserLoadPort userLoadPort;
    private final TeamIfExists teamIfExists;
    private final TeamUpdatePort teamUpdatePort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final BarcodeUpdatePort barcodeUpdatePort;
    private final AttendanceAddForUserPort attendanceAddForUserPort;

    public PanelAdminTeamController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        userSavePort = Container.getBean(UserSavePort.class);
        userUpdatePort = Container.getBean(UserUpdatePort.class);
        teamSavePort = Container.getBean(TeamSavePort.class);
        teamUpdatePort = Container.getBean(TeamUpdatePort.class);
        teamLoadPort = Container.getBean(TeamLoadPort.class);
        barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        barcodeUpdatePort = Container.getBean(BarcodeUpdatePort.class);
        userLoadPort = Container.getBean(UserLoadPort.class);
        teamDeletePort = Container.getBean(TeamDeletePort.class);
        teamIfExists = Container.getBean(TeamIfExists.class);
        attendanceAddForUserPort = Container.getBean(AttendanceAddForUserPort.class);
        addSubscriptionPort = Container.getBean(AddSubscriptionPort.class);
    }

    @FXML
    public void initialize() {
        configureTabTeam();
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Admin.");
        loadTableTeam();
    }

    private void configureTabTeam() {
        tableTeam.setRowFactory(tv -> {
            TableRow<TeamDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                TeamDetail team = row.getItem();
                if (team == null) {
                    return;
                }
                loadUserTeam();
            });
            return row;
        });

        tableUser.getColumns().get(0).setCellFactory(createBarcodeCellFactory());

        stretchColumnsToEqualSize(tableTeam);
        stretchColumnsToEqualSize(tableUser);
    }

    private void loadUserTeam() {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            List<UserDetail> collect = userLoadPort.findActiveUsersByTeamId(selectedItem.getTeamId()).stream().map(o -> {
                SubscriptStatus status = o.getSubscription().getStatus(LocalDate.now());
                return UserDetail.fromEntityToFull(o, LocalDate.now());
            }).collect(Collectors.toList());
            tableUser.getItems().setAll(collect);
        }
    }


    void loadTableTeam() {
        List<TeamDetail> teams = teamLoadPort.findAllActive().stream().map(en ->
                new TeamDetail(en.getTeamId(), en.getName(), en.getMembershipPayment())).collect(Collectors.toList());
        tableTeam.getItems().setAll(teams);
        tableUser.getItems().clear();
    }

    @FXML
    public void addTeamForm() throws IOException {
        FormTeamAddController controller = new FormTeamAddController(teamIfExists, teamSavePort);

        Pane root = loadFxml(FORM_TEAM_ADD, controller);
        createStage("Kreiraj novi tim", root, primaryStage).showAndWait();

        loadTableTeam();
    }

    @FXML
    public void updateTeamForm() throws IOException {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na izmeni.");
            return;
        }

        FormTeamUpdateController controller = new FormTeamUpdateController(selectedItem.getTeamId(), selectedItem.getName(), selectedItem.getFee());
        createStage("Kreiraj novi tim", loadFxml(FORM_TEAM_UPDATE, controller), primaryStage).showAndWait();

        loadTableTeam();
    }

    private boolean promptUserToContinueDeletion(String teamName) {
        AtomicBoolean shouldContinue = new AtomicBoolean(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje tima");
        alert.setHeaderText(String.format("Da li želiš da obrišeš tim %s?", teamName));
        alert.setContentText("Svi barkodovi asocirani sa ovim timom ce biti trajno deaktivirani");

        ButtonType yesButton = new ButtonType("Obriši");
        ButtonType noButton = new ButtonType("Nemoj da brišeš");
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.getDialogPane().getStylesheets().add(getClass().getResource(CSS).toExternalForm());
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                shouldContinue.set(true);
            }
        });

        return shouldContinue.get();
    }

    @FXML
    void teamDeleteForm() {
        TeamDetail selectedItem = tableTeam.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni na obrisi.");
            return;
        }


        if (promptUserToContinueDeletion(selectedItem.getName())) {
            ValidatorResponse res = teamDeletePort.validateAndDeleteTeam(selectedItem.getTeamId());
            if (res.hasErrors()) {
                ToastView.showModal(res.getErrorOrEmpty(TeamEntity.TEAM_ID));
                return;
            }

            ToastView.showModal(res.getMessage());
            loadTableTeam();
        }
    }

    @FXML
    void onAddAttendance(MouseEvent event) {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni.");
            return;
        }

        Long barcode = parseBarcodeStr(selectedItem.getBarcodeId());
        ValidatorResponse res = attendanceAddForUserPort.validateAndAddAttendance(barcode, LocalDateTime.now());
        if (res.isOk()) {
            BarcodeEntity en = barcodeLoadPort.findById(barcode).get();
            attendanceAddForUserPort.validateAndAddAttendance(en.getBarcodeId(), LocalDateTime.now());

            AlertShowAttendanceController alertCtrl = new AlertShowAttendanceController(en, LocalDate.now());
            Pane pane = loadFxml(ALERT_SHOW_ATTENDANCE, alertCtrl);
            ToastView.showModal(pane, 4000, 200);
        } else {
            ToastView.showModal(res.getMessage());
        }
    }

    @FXML
    void onPayNextMonth(MouseEvent event) {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni.");
            return;
        }

        ValidatorResponse res = addSubscriptionPort.addNextMonthSubscription(selectedItem.getBarcodeId());
        if (res.isOk()) {
            ToastView.showModal(res.getMessage());
            loadUserTeam();
        } else {
            ToastView.showModal(Messages.ERROR_ADDING_SUBSCRIPTION);
        }
    }

    @FXML
    void onShowSubscriptionDetails(MouseEvent event) {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi unos iz tabele pa onda klikni.");
            return;
        }

        BarcodeEntity barcode = barcodeLoadPort.findById(parseBarcodeStr(selectedItem.getBarcodeId())).get();
        AlertShowMembershipController alertCtrl = new AlertShowMembershipController(barcode, LocalDateTime.now());
        Pane pane = loadFxml(ALERT_SHOW_MEMBERSHIP, alertCtrl);
        createStage("Članarina", pane, primaryStage).showAndWait();

        loadUserTeam();
    }

    @FXML
    void onPrintBarcodeAsPic(MouseEvent event) throws WriterException, IOException {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            ToastView.showModal("Izaberi polaznika iz tabele pa onda klikni.");
            return;
        }

        String firstName = selectedItem.getFirstName();
        String lastName = selectedItem.getLastName();
        String fullName = String.format("%s %s", firstName, lastName);
        String formattedBarcode = selectedItem.getBarcodeId();

        BarcodeView barcodeView = BarcodeGenerator.generateBarcodeImage(selectedItem.getUserId(), 300, 100);

        byte[] bytes = generatePDF(fullName, formattedBarcode, selectedItem.getTeam(), barcodeView);

        try {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes));

            // Create a PDFRenderer object
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Convert the first page to a BufferedImage
            int pageIndex = 0; // You can change this index to convert a different page
            BufferedImage image = pdfRenderer.renderImage(pageIndex);

            // Save the BufferedImage as a JPG file
            File outputJpg = new File(String.format("%s.jpg", fullName));
            ImageIO.write(image, "jpg", outputJpg);

            System.out.println("Image saved as " + outputJpg.getAbsolutePath());

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static final String FONT = "resources/com/example/gambarucmsui/font/arial-unicode-ms.ttf";
    public static final Font serbianFont = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.CP1250, 40, Font.NORMAL);
    private byte[] generatePDF(String fullName, String barcodeId, String team, BarcodeView barcodeView) {
        Document document = new Document();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            int numColumns = 1;

            PdfPTable mainTable = new PdfPTable(numColumns);
            mainTable.setWidthPercentage(100);

            PdfPCell cellBarcode = new PdfPCell(mainTable);
            cellBarcode.setBorder(PdfPCell.NO_BORDER);
            cellBarcode.addElement(Image.getInstance(barcodeView.getBufferedImage(), null));

            PdfPCell barcodeIdCell = new PdfPCell();
            Paragraph barcodeIdText = new Paragraph(String.format("%s", barcodeId), serbianFont);
            barcodeIdText.setAlignment(Paragraph.ALIGN_CENTER);
            barcodeIdCell.addElement(barcodeIdText);
            barcodeIdCell.setBorder(PdfPCell.NO_BORDER);

            PdfPCell textCell = new PdfPCell();
            Paragraph pName = new Paragraph(String.format("%s", fullName), serbianFont);
            pName.setAlignment(Paragraph.ALIGN_CENTER);
            textCell.addElement(pName);
            textCell.setBorder(PdfPCell.NO_BORDER);

            PdfPCell teamCell = new PdfPCell();
            Paragraph tName = new Paragraph(String.format("%s", team), serbianFont);
            tName.setAlignment(Paragraph.ALIGN_CENTER);
            teamCell.addElement(tName);
            teamCell.setBorder(PdfPCell.NO_BORDER);

            mainTable.addCell(cellBarcode);
            mainTable.addCell(barcodeIdCell);
            mainTable.addCell(textCell);
            mainTable.addCell(teamCell);

            document.add(mainTable);
            document.close();

            System.out.println("PDF generated successfully.");
        } catch (DocumentException e) {
            System.out.println("PDF generation failed. Exception: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }
}
