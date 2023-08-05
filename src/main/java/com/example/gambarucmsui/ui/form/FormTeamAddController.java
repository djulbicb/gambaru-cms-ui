package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.TeamEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.Response;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
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
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeamName;

    public FormTeamAddController() {
        teamIfExists = Container.getBean(TeamIfExists.class);
        teamSavePort = Container.getBean(TeamSavePort.class);
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
    void onSave(MouseEvent event) {
        String paymentFeeStr = getOr(txtMembershipFee, "");
        String teamNameStr = getOr(txtTeamName, "");

        ValidatorResponse save = save(teamNameStr, paymentFeeStr);

        if (save.hasErrors()) {
            Map<String, String> errors = save.getErrors();
            if (errors.containsKey("name")) {
                lblErrTeamName.setText(errors.get("name"));
            }
            if (errors.containsKey("membershipPayment")) {
                lblErrMembershipFee.setText(errors.get("membershipPayment"));
            }
            return;
        }

        ToastView.showModal(save.getMessage());
        close();
    }

    public ValidatorResponse save(String teamNameStr, String paymentFeeStr) {
        ValidatorResponse verifySaveTeam = teamSavePort.verifySaveTeam(teamNameStr, paymentFeeStr);
        if (verifySaveTeam.isOk()) {
            String outTeamName = teamNameStr.trim();
            BigDecimal outMembershipPayment = BigDecimal.valueOf(Double.valueOf(paymentFeeStr.trim()));

            TeamEntity team = teamSavePort.save(outTeamName, outMembershipPayment);
            return new ValidatorResponse(TeamInputValidator.msgTeamIsCreated(team.getName()));
        }
        return verifySaveTeam;
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
