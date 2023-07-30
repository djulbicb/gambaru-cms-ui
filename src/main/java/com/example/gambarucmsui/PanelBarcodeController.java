package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.TeamEntity;
import com.example.gambarucmsui.adapter.out.persistence.entity.UserEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.*;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.util.generators.BarcodeGenerator;
import com.example.gambarucmsui.util.generators.BarcodeView;
import com.example.gambarucmsui.util.generators.PDFGenerator;
import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.gambarucmsui.util.DataUtil.*;

public class PanelBarcodeController implements PanelHeader {
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;
    private final UserRepository userRepo;
    private final UserMembershipRepository membershipRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final Stage primaryStage;

    public PanelBarcodeController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
        this.barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
        this.teamRepo = (TeamRepository) repositoryMap.get(TeamRepository.class);
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        this.primaryStage = primaryStage;
    }

    @FXML
    private Button savePdf;
    @FXML
    private TextArea txtBarcodes;
    @FXML
    private Button btnFetchBarcodes;
    @FXML
    private Button btnFetchNewBarcodes;

    @FXML
    public void initialize() {
        System.out.println("Panel barcode");
    }

    @Override
    public void viewSwitched() {

    }

    @FXML
    public void fetchBarcodes() {
        List<BarcodeEntity> barcodeEntities = barcodeRepo.fetchOrGenerateBarcodes(100, BarcodeEntity.Status.NOT_USED);
        List<Long> ids = barcodeEntities.stream().map(barcodeEntity -> barcodeEntity.getBarcodeId()).collect(Collectors.toList());
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
        List<BarcodeEntity> barcodeEntities = barcodeRepo.generateNewBarcodes(100);
        List<Long> ids = barcodeEntities.stream().map(barcodeEntity -> barcodeEntity.getBarcodeId()).collect(Collectors.toList());
        String csv = listToCsv(ids);
        txtBarcodes.setText(csv);
    }

    @FXML
    private void printBarcodesToPdf() throws WriterException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload File Path");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        Stage ownerStage = (Stage) savePdf.getScene().getWindow();
        File file = fileChooser.showSaveDialog(ownerStage);

        List<BarcodeView> views = new ArrayList<>();
        List<Long> collect = Arrays.stream(txtBarcodes.getText().split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<BarcodeEntity> barcodeId = barcodeRepo.findByIds("barcodeId", collect);

        for (BarcodeEntity barcodeEntity : barcodeId) {
            BarcodeView barcodeView = BarcodeGenerator.generateBarcodeImage(barcodeEntity.getBarcodeId(), 300, 100);
            views.add(barcodeView);
        }


        byte[] bytes = PDFGenerator.generatePDF(views);
        Files.write(Path.of(file.getAbsolutePath()), bytes, StandardOpenOption.CREATE);
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

        // Start the process in a separate thread using Task
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int count = getCount();
                for (int i = 0; i < count; i++) {
                    List<BarcodeEntity> barcodes = barcodeRepo.findAll();
                    List<LocalDateTime> times = new ArrayList<>();
                    for (BarcodeEntity barcode : barcodes) {
                        times.add(getDateTime());
                    }
                    attendanceRepo.saveNewAll(barcodes, times);
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
                    List<BarcodeEntity> barcodes = barcodeRepo.findAll();
                    for (BarcodeEntity barcode : barcodes) {
                        LocalDateTime dateTime = getDateTime();
                        membershipRepo.saveNew(barcode, dateTime.getMonthValue(), dateTime.getYear(), dateTime, getDecimal());
                    }
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
    void onAddTeams(MouseEvent event) {
        System.out.println("Attendance");
        TeamEntity team = teamRepo.saveNewTeam(getTeamName(), getDecimal());
        ToastView.showModal(String.format("Tim %s dodat.", team.getName()));
    }

    @FXML
    void onAddUserToTeam(MouseEvent event) {
        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<UserEntity> users = userRepo.findAll();
                List<TeamEntity> teams = teamRepo.findAll();
                for (UserEntity user : users) {
                    BarcodeEntity barcode = barcodeRepo.fetchOneOrGenerate(BarcodeEntity.Status.ASSIGNED);
                    TeamEntity team = pickRandom(teams);

                    barcodeRepo.updateBarcodeWithUserAndTeam(barcode, user, team);
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
    void onAddUsers(MouseEvent event) {
        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                int count = getCount();
                for (int i = 0; i < count; i++) {
                    if (getBoolean()) {
                        userRepo.saveOne(getFemaleName(), getSurname(), UserEntity.Gender.FEMALE, getPhone(), getDateTime());
                    } else {
                        userRepo.saveOne(getMaleName(), getSurname(), UserEntity.Gender.MALE, getPhone(), getDateTime());
                    }
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
