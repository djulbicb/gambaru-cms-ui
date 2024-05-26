package com.example.gambarucmsui.ui.panel;


import com.example.gambarucmsui.common.Messages;
import com.example.gambarucmsui.database.entity.BarcodeEntity;
import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeUpdatePort;
import com.example.gambarucmsui.ports.interfaces.membership.AddMembershipForBarcode;
import com.example.gambarucmsui.ports.interfaces.membership.LoadPersonMembership;
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
import com.example.gambarucmsui.ui.dto.core.MembershipFeeDetail;
import com.example.gambarucmsui.ui.dto.core.UserDetail;
import com.example.gambarucmsui.ui.form.*;
import com.example.gambarucmsui.ui.text.Labels;
import com.example.gambarucmsui.util.DataUtil;
import com.example.gambarucmsui.util.generators.BarcodeGenerator;
import com.example.gambarucmsui.util.generators.BarcodeView;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
    @FXML
    private Button btnPayNextMonth;

    private final Stage primaryStage;
    private final UserSavePort userSavePort;
    private final TeamDeletePort teamDeletePort;
    private final TeamLogoLoadPort teamLogoLoadPort;
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
    private final AddMembershipForBarcode addMembershipForBarcode;

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
        teamLogoLoadPort = Container.getBean(TeamLogoLoadPort.class);
        addMembershipForBarcode = Container.getBean(AddMembershipForBarcode.class);
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


        tableUser.setRowFactory(tv -> {
            TableRow<UserDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                UserDetail u = row.getItem();
                if (u == null) {
                    return;
                }
                String feeStr = Labels.payNextMonth(u.getMembershipFee());
                btnPayNextMonth.setText(feeStr);

            });
            return row;
        });



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
        btnPayNextMonth.setText(Labels.payNextMonth());
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
            BarcodeEntity b = barcodeLoadPort.findById(barcode).get();
            AlertShowAttendanceController alertCtrl = new AlertShowAttendanceController(b, LocalDate.now());
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

        BarcodeEntity barcode = barcodeLoadPort.findById(selectedItem.getBarcodeIdNum()).get();
        if (barcode.getSubscription().isFreeOfCharge()) {
            ToastView.showModal("Korisnik besplatno trenira..");
            return;
        }

        ValidatorResponse res = addSubscriptionPort.addNextMonthSubscription(selectedItem.getBarcodeId());
        if (res.isOk()) {
            int fee = DataUtil.deductFee(barcode.getTeam().getMembershipPayment(), barcode.getDiscount());
            addMembershipForBarcode.addMembership(barcode.getBarcodeId(), LocalDateTime.now(), fee);
            ToastView.showModal(res.getMessage());
            loadUserTeam();
        } else {
            ToastView.showModal(Messages.ERROR_ADDING_SUBSCRIPTION);
        }
    }


    @FXML
    protected void addBarcodeDiscount() throws IOException {
        UserDetail selectedItem = tableUser.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            ToastView.showModal("Izaberi korisnika iz tabele pa onda klikni.");
            return;
        }

        FormBarcodeGetDiscount controller = new FormBarcodeGetDiscount();

        Pane root = loadFxml(FORM_BARCODE_GET_DISCOUNT, controller);
        createStage("Popust na članarinu", root, primaryStage).showAndWait();

        if (controller.isReady()) {
            int discount = controller.getDiscount();
            BarcodeEntity barcode = barcodeLoadPort.findById(parseBarcodeStr(selectedItem.getBarcodeId())).get();
            barcode.setDiscount(discount);
            barcodeUpdatePort.updateDiscount(barcode.getBarcodeId(), discount);

            ToastView.showModal(String.format("Popust od %s dodat.", discount));
            loadUserTeam();
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

        Long teamId = teamLoadPort.findByName(selectedItem.getTeam()).getTeamId();
        BufferedImage imageView = teamLogoLoadPort.loadTeamLogoByTeamId(teamId);

        BarcodeView barcodeView = BarcodeGenerator.generateBarcodeImage(selectedItem.getBarcodeIdNum(), 300, 65);

        byte[] bytes = generatePDF(fullName, formattedBarcode, selectedItem.getTeam(), barcodeView, imageView);

        try {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes));
            document.save("test.pdf");

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
    public static final Font serbianFont = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.CP1250, 25, Font.NORMAL);
    private byte[] generatePDF(String fullName, String barcodeId, String team, BarcodeView barcodeView, BufferedImage logoView) {
        Document document = new Document(new RectangleReadOnly(420.0F, 650.0F)); // new Rectangle(250, 600)

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            int numColumns = 1;

            PdfPTable mainTable = new PdfPTable(numColumns);
            mainTable.setWidthPercentage(100);

            PdfPCell teamLogoCell = new PdfPCell(mainTable);
            teamLogoCell.setBorder(PdfPCell.NO_BORDER);
            teamLogoCell.addElement(Image.getInstance(logoView, null));
            teamLogoCell.setPaddingBottom(10f);
            mainTable.completeRow();

            PdfPCell cellBarcode = new PdfPCell(mainTable);
            cellBarcode.setBorder(PdfPCell.NO_BORDER);
            cellBarcode.addElement(Image.getInstance(barcodeView.getBufferedImage(), null));
            cellBarcode.setPadding(0f);
            mainTable.completeRow();

            PdfPCell barcodeIdCell = new PdfPCell();
            Paragraph barcodeIdText = new Paragraph(String.format("%s", barcodeId), serbianFont);
            barcodeIdText.setAlignment(Paragraph.ALIGN_CENTER);
            barcodeIdCell.addElement(barcodeIdText);
            barcodeIdCell.setBorder(PdfPCell.NO_BORDER);
            mainTable.completeRow();

            PdfPCell textCell = new PdfPCell();
            Paragraph pName = new Paragraph(String.format("%s", fullName), serbianFont);
            pName.setAlignment(Paragraph.ALIGN_CENTER);
            textCell.setPadding(0f);
            textCell.setPaddingTop(0);
            textCell.addElement(pName);
            textCell.setBorder(PdfPCell.NO_BORDER);
            mainTable.completeRow();

            PdfPCell teamCell = new PdfPCell();
            Paragraph tName = new Paragraph(String.format("%s", team), serbianFont);
            tName.setAlignment(Paragraph.ALIGN_CENTER);
            teamCell.setPadding(0f);
            teamCell.addElement(tName);
            teamCell.setBorder(PdfPCell.NO_BORDER);
            mainTable.completeRow();

            mainTable.addCell(teamLogoCell);
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
