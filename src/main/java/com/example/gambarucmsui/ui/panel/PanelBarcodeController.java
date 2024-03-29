package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.interfaces.attendance.AttendanceAddForUserPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeLoadPort;
import com.example.gambarucmsui.ports.interfaces.barcode.BarcodeFetchOrGeneratePort;
import com.example.gambarucmsui.ports.interfaces.team.TeamLoadPort;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
import com.example.gambarucmsui.ports.interfaces.user.UserAddToTeamPort;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.UserSavePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.util.FormatUtil;
import com.example.gambarucmsui.util.generators.BarcodeGenerator;
import com.example.gambarucmsui.util.generators.BarcodeView;
import com.example.gambarucmsui.util.generators.PDFGenerator;
import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.common.Props.FUTURE;
import static com.example.gambarucmsui.common.Props.PAST;
import static com.example.gambarucmsui.util.DataUtil.*;
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class PanelBarcodeController implements PanelHeader {
    private final Stage primaryStage;
    private final UserSavePort userSavePort;

    private final TeamSavePort teamSavePort;
    private final AttendanceAddForUserPort addAttendance;
    private final UserAddToTeamPort userAddToTeamPort;
    private final TeamLoadPort teamLoadPort;
    private final UserLoadPort userLoadPort;
    private final BarcodeLoadPort barcodeLoadPort;
    private final BarcodeFetchOrGeneratePort barcodeFetchOrGeneratePort;

    public PanelBarcodeController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.teamSavePort = Container.getBean(TeamSavePort.class);
        this.userSavePort = Container.getBean(UserSavePort.class);
        this.addAttendance = Container.getBean(AttendanceAddForUserPort.class);
        this.userAddToTeamPort = Container.getBean(UserAddToTeamPort.class);
        this.teamLoadPort = Container.getBean(TeamLoadPort.class);
        this.userLoadPort = Container.getBean(UserLoadPort.class);
        this.barcodeLoadPort = Container.getBean(BarcodeLoadPort.class);
        this.barcodeFetchOrGeneratePort = Container.getBean(BarcodeFetchOrGeneratePort.class);
    }

    @FXML private Button savePdf;
    @FXML private TextArea txtBarcodes;
    @FXML private TextField txtBarcodeCount;
    @FXML private Button btnFetchBarcodes;
    @FXML private Button btnFetchNewBarcodes;

    @FXML
    public void initialize() {
        System.out.println("Panel barcode");
    }

    @Override
    public void viewSwitched() {
        System.out.println("Switched to panel Barcode.");
    }

    @FXML
    public void fetchBarcodes() {
        List<BarcodeEntity> barcodeEntities = barcodeLoadPort.findAllByStatus(BarcodeEntity.Status.NOT_USED);
        List<Long> ids = barcodeEntities.stream().map(BarcodeEntity::getBarcodeId).collect(Collectors.toList());
        String csv = listToCsv(ids);
        txtBarcodes.setText(csv);
    }

    private String listToCsv(List ids) {
        StringJoiner csvJoiner = new StringJoiner(",");
        for (Object word : ids) {
            csvJoiner.add(word.toString());
        }
        return csvJoiner.toString();
    }

    @FXML
    private void fetchNewBarcodes() {
        System.out.println("Adding attendance");

        String countStr = txtBarcodeCount.getText();
        if (!FormatUtil.isInteger(countStr)) {
            ToastView.showModal("Upiši koliko barkodova da se kreira");
            return;
        }

        Integer count = Integer.valueOf(countStr);
        if (count < 1 || count > 1000) {
            ToastView.showModal("Upiši broj između 1 i 1000.");
            return;
        }

        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<BarcodeEntity> barcodeEntities = barcodeFetchOrGeneratePort.generateNewBarcodes(count);
                List<Long> ids = barcodeEntities.stream().map(barcodeEntity -> barcodeEntity.getBarcodeId()).collect(Collectors.toList());
                String csv = listToCsv(ids);
                txtBarcodes.setText(csv);

                Platform.runLater(() -> {
                    closeAlertDialog();
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void printBarcodesToPdf() throws WriterException, IOException {
        if (txtBarcodes.getText().isBlank()) {
            ToastView.showModal("Barkod panel je prazan. Upiši barkodove npr. 123,654,789");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload File Path");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        List<BarcodeView> views = new ArrayList<>();
        List<Long> collect = new ArrayList<>();
        for (String barcode : txtBarcodes.getText().split(",")) {
            if (barcode == null || barcode.isBlank()) {
                continue;
            }
            if (!isLong(barcode.trim())) {
                ToastView.showModal(String.format("Ovo nije broj %s. Akcija je prekinuta", barcode.trim()));
                return;
            }
            collect.add(parseBarcodeStr(barcode.trim()));
        }

        Stage ownerStage = (Stage) savePdf.getScene().getWindow();
        File file = fileChooser.showSaveDialog(ownerStage);
        if (file == null) {
            return;
        }
        List<BarcodeEntity> barcodeId = barcodeLoadPort.findByIds(collect);

        for (BarcodeEntity barcodeEntity : barcodeId) {
            BarcodeView barcodeView = BarcodeGenerator.generateBarcodeImage(barcodeEntity.getBarcodeId(), 300, 100);
            views.add(barcodeView);
        }


        byte[] bytes = PDFGenerator.generatePDF(views);
        String path = file.getAbsolutePath();

        if (!file.getName().endsWith(".pdf")) {
            path += path + ".pdf";
        }


        Files.write(Path.of(path), bytes, StandardOpenOption.CREATE);
    }
    //  GENERATE RANDOM
    ////////////////////////////////////////////////////////

    @FXML
    private TextField txtCount;

    private int getCount() {
        int defaultVal = 10;
        if (txtCount.getText().isBlank()) {
            return defaultVal;
        }

        try {
            return Integer.parseInt(txtCount.getText().trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    @FXML
    void onAddAttendance(MouseEvent event) {
        System.out.println("Adding attendance");
        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int count = getCount();
                for (int i = 0; i < count; i++) {
                    List<BarcodeEntity> barcodes = barcodeLoadPort.findAllByStatus(BarcodeEntity.Status.ASSIGNED);
                    for (BarcodeEntity barcode : barcodes) {
                        addAttendance.addToSaveBulk(barcode, getDateTime());
                    }
                    addAttendance.executeBulk();
                }

                Platform.runLater(() -> {
                    closeAlertDialog();
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    void onAddPayment(MouseEvent event) {
        System.out.println("Adding Membership");
        showAlertDialog("Dodavanje plaćanja.", "Sačekaj...");

        // Start the process in a separate thread using Task
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int count = getCount();
                for (int i = 0; i < count; i++) {
//                    List<BarcodeEntity> barcodes = barcodeLoadPort.findAllByStatus(BarcodeEntity.Status.ASSIGNED);
//                    for (BarcodeEntity barcode : barcodes) {
//                        addMembership.addToSaveBulkMembership(barcode, getDateTime(), getDecimal());
//                    }
//                    addMembership.executeBulkMembership();
                }

                Platform.runLater(() -> {
                    closeAlertDialog();
                });
                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    void onAddTeams(MouseEvent event) throws IOException {
        System.out.println("Kreiranje novog tima");
        teamSavePort.save(getTeamName(), getDecimal(), createBlackImageByteArray());
    }

    @FXML
    void onAddUserToTeam(MouseEvent event) {
        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<PersonEntity> users = userLoadPort.findAll();
                List<TeamEntity> teams = teamLoadPort.findAllActive();
                List<BarcodeEntity> barcodes = new ArrayList<>();
                for (PersonEntity user : users) {
                    BarcodeEntity barcode = barcodeFetchOrGeneratePort.fetchOneOrGenerate(BarcodeEntity.Status.ASSIGNED);
                    TeamEntity team = pickRandom(teams);
                    userAddToTeamPort.verifyAndAddUserToPort(user.getPersonId(), String.valueOf(barcode.getBarcodeId()), team.getName(), false, PAST, FUTURE);
                }
//                barcodeRepo.saveMultiple(barcodes); TODO
                Platform.runLater(() -> {
                    closeAlertDialog();
                });

                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    void onSaveScenarioUsers(ActionEvent event) throws IOException {
        teamSavePort.verifyAndSaveTeam("Delete", "123", null);

        userSavePort.save("Null","Null", PersonEntity.Gender.MALE, "123", null);
        userSavePort.save("Bez slike","Bez Slike", PersonEntity.Gender.MALE, "123", null);
        userSavePort.save("Sa slikom","Sa slikom", PersonEntity.Gender.MALE, "123", null);
        userSavePort.save("Free","Free", PersonEntity.Gender.FEMALE, "123", createBlackImageByteArray());
        userSavePort.save("Next","Next", PersonEntity.Gender.FEMALE, "123", createBlackImageByteArray());
        userSavePort.save("Delete","Delete", PersonEntity.Gender.FEMALE, "123", createBlackImageByteArray());
        userSavePort.save("Disable","Disable", PersonEntity.Gender.FEMALE, "123", createBlackImageByteArray());
        userSavePort.save("Re-enable","Re-enable", PersonEntity.Gender.FEMALE, "123", createBlackImageByteArray());
    }

    public static byte[] createBlackImageByteArray() {
        int IMAGE_WIDTH = 30;
        int IMAGE_HEIGHT = 30;

        BufferedImage blackImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = blackImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        g.dispose();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(blackImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    void onAddUsers(MouseEvent event) {
        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                int count = getCount();
                List<PersonEntity> users = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    if (getBoolean()) {
                        userSavePort.addToBulkSave(getFemaleName(), getSurname(), PersonEntity.Gender.FEMALE, getPhone());
                    } else {
                        userSavePort.addToBulkSave(getMaleName(), getSurname(), PersonEntity.Gender.MALE, getPhone());
                    }
                    userSavePort.executeBulkSave();
                }

                Platform.runLater(() -> {
                    closeAlertDialog();
                });

                return null;
            }
        };

        new Thread(task).start();
    }

    Optional<Alert> alertOpt = Optional.empty();

    private void showAlertDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.show();

        alertOpt = Optional.of(alert);
    }

    // Method to close the alert popup
    private void closeAlertDialog() {
        if (alertOpt.isPresent()) {
            alertOpt.get().close();
        }
    }
}
