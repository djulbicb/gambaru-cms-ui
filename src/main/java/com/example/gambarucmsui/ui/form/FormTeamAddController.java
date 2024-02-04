package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.team.TeamIfExists;
import com.example.gambarucmsui.ports.interfaces.team.TeamSavePort;
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
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormTeamAddController implements Initializable {
    // Repo
    //////////////////////////////////////////
    private TeamInputValidator validator = new TeamInputValidator();
    private final TeamIfExists teamIfExists;
    private final TeamSavePort teamSavePort;
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

    public FormTeamAddController(TeamIfExists teamIfExists, TeamSavePort teamSavePort) {
        this.teamIfExists = teamIfExists;
        this.teamSavePort = teamSavePort;
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
    void onSave(MouseEvent event) throws IOException {
        String paymentFeeStr = getOr(txtMembershipFee, "");
        String teamNameStr = getOr(txtTeamName, "");

        ValidatorResponse res = teamSavePort.verifyAndSaveTeam(teamNameStr, paymentFeeStr, outLogoData);

        if (res.hasErrors()) {
            lblErrTeamName.setText(res.getErrorOrEmpty(TeamEntity.TEAM_NAME));
            lblErrMembershipFee.setText(res.getErrorOrEmpty(TeamEntity.MEMBERSHIP_PAYMENT));
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
    @FXML
    private void close() {
        root.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
