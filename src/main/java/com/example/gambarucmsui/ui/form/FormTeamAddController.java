package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.repo.TeamRepository;
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
import java.util.ResourceBundle;

public class FormTeamAddController implements Initializable {
    // Repo
    //////////////////////////////////////////
    private final TeamRepository teamRepo;
    private TeamInputValidator validator = new TeamInputValidator();
    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrMembershipFee;
    @FXML private Label lblErrTeamName;
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeamName;

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady = false;
    private BigDecimal outMembershipPayment;
    private String outTeamName;

    public FormTeamAddController(TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
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
        String paymentFeeStr = txtMembershipFee.getText().trim();
        String teamNameStr = txtTeamName.getText().trim();

        if (validate(teamNameStr, paymentFeeStr)) {
            isFormReady = true;
            outMembershipPayment = BigDecimal.valueOf(Double.valueOf(paymentFeeStr));
            outTeamName = teamNameStr;
            close();
        }
    }

    private boolean validate(String teamNameStr, String paymentFeeStr) {
        boolean isFormCorrect = true;
        if (!validator.isTeamNameValid(teamNameStr)) {
            lblErrTeamName.setText(validator.errTeamName());
            isFormCorrect = false;
        }
        if (teamRepo.ifTeamNameExists(teamNameStr)) {
            lblErrTeamName.setText(validator.errTeamNameExists());
            isFormCorrect = false;
        }
        if (!validator.isFeeValid(paymentFeeStr)) {
            lblErrMembershipFee.setText(validator.errTeamFee());
            isFormCorrect = false;
        }

        return isFormCorrect;
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public FormTeamAddController.Data getData () {
        return new Data(outMembershipPayment, outTeamName);
    }

    public static class Data {
        private BigDecimal membershipPaymentFee;
        private String teamName;

        public Data(BigDecimal membershipPaymentFee, String teamName) {
            this.membershipPaymentFee = membershipPaymentFee;
            this.teamName = teamName;
        }

        public BigDecimal getMembershipPaymentFee() {
            return membershipPaymentFee;
        }

        public String getTeamName() {
            return teamName;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "membershipPaymentFee=" + membershipPaymentFee +
                    ", teamName='" + teamName + '\'' +
                    '}';
        }
    }
}
