package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.team.TeamIfExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamUpdatePort;
import com.example.gambarucmsui.ui.ToastView;
import com.example.gambarucmsui.ui.form.validation.TeamInputValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormTeamUpdateController implements Initializable {
    private final TeamIfExists teamIfExists;
    private final TeamUpdatePort teamUpdate;
    private final Long inTeamId;
    private final String inTeamName;
    private final BigDecimal inMembershipPayment;
    private TeamInputValidator validator = new TeamInputValidator();
    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrMembershipFee;
    @FXML private Label lblErrTeamName;
    @FXML private Label lblErrLogo;
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeamName;

    // OUTPUT DATA
    /////////////////////////////////////////
    private byte[] outLogoData;

    public FormTeamUpdateController(Long inTeamId, String inTeamName, BigDecimal inMembershipPayment) {
        this.inTeamId = inTeamId;
        this.inTeamName = inTeamName;
        this.inMembershipPayment = inMembershipPayment;

        teamIfExists = Container.getBean(TeamIfExists.class);
        teamUpdate = Container.getBean(TeamUpdatePort.class);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtTeamName.setText(inTeamName);
        txtMembershipFee.setText(String.valueOf(inMembershipPayment));
    }

    @FXML
    void txtMembershipFeeReset(KeyEvent event) {
        lblErrMembershipFee.setText("");
    }

    @FXML
    void txtTeamNameReset(KeyEvent event) {
        lblErrTeamName.setText("");
    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onUpdate(MouseEvent event) {

        String teamNameStr = getOr(txtTeamName, "");
        String paymentFeeStr = getOr(txtMembershipFee, "");


        ValidatorResponse res = teamUpdate.verifyAndUpdateTeam(inTeamId, teamNameStr, paymentFeeStr, outLogoData);
        if (res.hasErrors()) {
            lblErrTeamName.setText(res.getErrorOrEmpty("name"));
            lblErrMembershipFee.setText(res.getErrorOrEmpty("membershipPayment"));
            return;
        }

        ToastView.showModal(res.getMessage());
        close();
    }

    @FXML
    private void onAddLogo() throws IOException {
        System.out.println("logo");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Stack Trace");
        File file = fileChooser.showOpenDialog(null);
        if (file != null && file.exists()) {
            lblErrLogo.setText("");
            boolean isImage = file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg");
            if (!isImage) {
                lblErrLogo.setText("Mogu samo jpg i png slike.");
                return;
            }
            outLogoData = Files.readAllBytes(file.toPath());
        }
    }

    private void close() {
        root.getScene().getWindow().hide();
    }
}
