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

import static com.example.gambarucmsui.util.FormatUtil.isDecimal;
import static com.example.gambarucmsui.util.LayoutUtil.getOr;

public class FormTeamUpdateController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final TeamRepository teamRepo;
    private final Data inputData;
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
    private BigDecimal membershipPayment;
    private String teamName;

    public FormTeamUpdateController(FormTeamUpdateController.Data inputFormData, TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
        this.inputData = inputFormData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtTeamName.setText(inputData.getTeamName());
        txtMembershipFee.setText(inputData.getMembershipPaymentFee().toString());
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

        if (validate(teamNameStr, paymentFeeStr)) {
            isFormReady = true;
            membershipPayment = BigDecimal.valueOf(Double.valueOf(paymentFeeStr));
            teamName = teamNameStr;
            close();
        }
    }

    private boolean validate(String teamNameStr, String paymentFeeStr) {
        boolean isFormCorrect = true;
        if (!validator.isTeamNameValid(teamNameStr)) {
            lblErrTeamName.setText(validator.errTeamName());
            isFormCorrect = false;
        }
        if (!teamNameStr.equals(inputData.getTeamName()) && teamRepo.ifTeamNameExists(teamNameStr)) {
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

    public boolean isFormReady() {
        return isFormReady;
    }

    public FormTeamUpdateController.Data getData () {
        return new Data( membershipPayment, teamName);
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
                    ", membershipPaymentFee=" + membershipPaymentFee +
                    ", teamName='" + teamName + '\'' +
                    '}';
        }
    }
}
