package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.adapter.out.persistence.repo.TeamRepository;
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

public class FormTeamAddController implements Initializable {
    // Repo
    //////////////////////////////////////////
    private final TeamRepository teamRepo;
    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrMembershipFee;
    @FXML private Label lblErrTeamName;
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeamName;

    // FORM DATA
    /////////////////////////////////////////
    private boolean isFormReady = false;
    private BigDecimal membershipPayment;
    private String teamName;

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
        boolean isFormCorrect = true;

        String paymentFeeStr = txtMembershipFee.getText().trim();
        String teamNameStr = txtTeamName.getText().trim();

        if (teamNameStr.isBlank()) {
            lblErrTeamName.setText("Upiši ime tima");
            isFormCorrect = false;
        }
        if (teamRepo.ifTeamNameExists(teamNameStr)) {
            lblErrTeamName.setText("Takvo ime tima već postoji. Upiši drugačije ime.");
            isFormCorrect = false;
        }
        if (paymentFeeStr.isBlank() || !isDecimal(paymentFeeStr)) {
            lblErrMembershipFee.setText("Upiši cenu članarine. Npr 4000");
            isFormCorrect = false;
        }

        if (!isFormCorrect) {
            return;
        }

        isFormReady = true;
        membershipPayment = BigDecimal.valueOf(Double.valueOf(paymentFeeStr));
        teamName = teamNameStr;

        close();
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
        return new Data(membershipPayment, teamName);
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
