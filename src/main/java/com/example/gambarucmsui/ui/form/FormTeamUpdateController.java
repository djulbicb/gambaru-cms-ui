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

public class FormTeamUpdateController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final TeamRepository teamRepo;
    private final Data inputData;
    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrMembershipFee;
    @FXML private Label lblErrTeamName;
    @FXML private Label lblErrTeamId;
    @FXML private TextField txtTeamId;
    @FXML private TextField txtMembershipFee;
    @FXML private TextField txtTeamName;

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady = false;
    private Long teamId;
    private BigDecimal membershipPayment;
    private String teamName;

    public FormTeamUpdateController(FormTeamUpdateController.Data inputFormData, TeamRepository teamRepo) {
        this.teamRepo = teamRepo;
        this.inputData = inputFormData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtTeamId.setText(inputData.getTeamId().toString());
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
        boolean isFormCorrect = true;

        String paymentFeeStr = txtMembershipFee.getText().trim();
        String teamNameStr = txtTeamName.getText().trim();
        String teamIdStr = txtTeamId.getText().trim();

        if (teamNameStr.isBlank()) {
            lblErrTeamName.setText("Upiši ime tima");
            isFormCorrect = false;
        }
        if (!teamNameStr.equals(inputData.getTeamName()) && teamRepo.ifTeamNameExists(teamNameStr)) {
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
        teamId = Long.valueOf(teamIdStr);
        close();
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    public boolean isFormReady() {
        return isFormReady;
    }

    public FormTeamUpdateController.Data getData () {
        return new Data(teamId, membershipPayment, teamName);
    }

    public static class Data {
        private Long teamId;
        private BigDecimal membershipPaymentFee;
        private String teamName;

        public Data(Long teamId, BigDecimal membershipPaymentFee, String teamName) {
            this.teamId = teamId;
            this.membershipPaymentFee = membershipPaymentFee;
            this.teamName = teamName;
        }

        public BigDecimal getMembershipPaymentFee() {
            return membershipPaymentFee;
        }

        public String getTeamName() {
            return teamName;
        }

        public Long getTeamId() {
            return teamId;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "teamId=" + teamId +
                    ", membershipPaymentFee=" + membershipPaymentFee +
                    ", teamName='" + teamName + '\'' +
                    '}';
        }
    }
}
