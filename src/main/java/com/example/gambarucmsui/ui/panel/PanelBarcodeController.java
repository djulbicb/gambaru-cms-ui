package com.example.gambarucmsui.ui.panel;

import com.example.gambarucmsui.database.entity.*;
import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.Response;
import com.example.gambarucmsui.ports.user.*;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.util.DataUtil;
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
import static com.example.gambarucmsui.util.FormatUtil.isLong;
import static com.example.gambarucmsui.util.FormatUtil.parseBarcodeStr;

public class PanelBarcodeController implements PanelHeader {
    private final BarcodeRepository barcodeRepo;
    private final TeamRepository teamRepo;
    private final UserRepository userRepo;
    private final UserMembershipRepository membershipRepo;
    private final UserAttendanceRepository attendanceRepo;
    private final Stage primaryStage;
    private final UserSavePort userSavePort;

    private final TeamSavePort teamSavePort;
    private final AddUserAttendance addAttendance;
    private final AddUserMembership addMembership;
    private final UserAddToTeamPort userAddToTeamPort;
    public PanelBarcodeController(Stage primaryStage, HashMap<Class, Object> repositoryMap) {
        this.barcodeRepo = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
        this.teamRepo = (TeamRepository) repositoryMap.get(TeamRepository.class);
        this.userRepo = (UserRepository) repositoryMap.get(UserRepository.class);
        this.attendanceRepo = (UserAttendanceRepository) repositoryMap.get(UserAttendanceRepository.class);
        this.membershipRepo = (UserMembershipRepository) repositoryMap.get(UserMembershipRepository.class);
        this.primaryStage = primaryStage;

        teamSavePort = Container.getBean(TeamSavePort.class);
        userSavePort = Container.getBean(UserSavePort.class);
        addAttendance = Container.getBean(AddUserAttendance.class);
        addMembership = Container.getBean(AddUserMembership.class);
        userAddToTeamPort = Container.getBean(UserAddToTeamPort.class);
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
        System.out.println("Switched to panel Barcode.");
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
                    List<BarcodeEntity> barcodes = barcodeRepo.findAllByStatus(BarcodeEntity.Status.ASSIGNED);
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
                    List<BarcodeEntity> barcodes = barcodeRepo.findAllByStatus(BarcodeEntity.Status.ASSIGNED);
                    for (BarcodeEntity barcode : barcodes) {
                        addMembership.addToSaveBulkMembership(barcode, getDateTime(), getDecimal());
                    }
                    addMembership.executeBulkMembership();
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
        System.out.println("Kreiranje novog tima");
        Response<TeamEntity> save = teamSavePort.save(getTeamName(), getDecimal());
        ToastView.showModal(save.getMessage());
    }

    @FXML
    void onAddUserToTeam(MouseEvent event) {
        showAlertDialog("Dodavanje ljudi", "Sačekaj...");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<UserEntity> users = userRepo.findAll();
                List<TeamEntity> teams = teamRepo.findAllActive();
                List<BarcodeEntity> barcodes = new ArrayList<>();
                for (UserEntity user : users) {
                    BarcodeEntity barcode = barcodeRepo.fetchOneOrGenerate(BarcodeEntity.Status.ASSIGNED);
                    TeamEntity team = pickRandom(teams);

                    userAddToTeamPort.addUserToPort(user.getUserId(), barcode.getBarcodeId(), team.getName());
                }
                barcodeRepo.saveMultiple(barcodes);
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
                List<UserEntity> users = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    if (getBoolean()) {
                        userSavePort.addToBulkSave(getFemaleName(), getSurname(), UserEntity.Gender.FEMALE, getPhone(), getDateTime());
                    } else {
                        userSavePort.addToBulkSave(getMaleName(), getSurname(), UserEntity.Gender.MALE, getPhone(), getDateTime());
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
