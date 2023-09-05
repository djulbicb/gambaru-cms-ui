package com.example.gambarucmsui.ui.form;

import com.example.gambarucmsui.database.entity.PersonEntity;
import com.example.gambarucmsui.ports.Container;
import com.example.gambarucmsui.ports.ValidatorResponse;
import com.example.gambarucmsui.ports.interfaces.user.UserLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.UserUpdatePort;
import com.example.gambarucmsui.ui.form.validation.UserInputValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

public class FormUserUpdateController implements Initializable {
    // Input
    //////////////////////////////////////////
    private final UserInputValidator validator = new UserInputValidator();
    private final UserUpdatePort userUpdatePort;
    private final UserLoadPort userLoadPort;
    private final Long userId;

    // FXML
    //////////////////////////////////////////
    @FXML private VBox root;
    @FXML private Label lblErrUserFirstName;
    @FXML private Label lblErrUserLastName;
    @FXML private Label lblErrUserPhone;
    @FXML private Label lblErrUserGender;
    @FXML private ComboBox<String> cmbUserGender;
    @FXML private TextField txtUserFirstName;
    @FXML private TextField txtUserLastName;
    @FXML private TextField txtUserPhone;
    @FXML protected Label lblErrPicture;
    public FormUserUpdateController(Long userId) {
        this.userUpdatePort = Container.getBean(UserUpdatePort.class);
        this.userLoadPort = Container.getBean(UserLoadPort.class);
        this.userId = userId;
    }

    // OUTPUT DATA
    /////////////////////////////////////////
    private boolean isFormReady;
    private String outFirstName;
    private String outLastName;
    private String outPhone;
    private PersonEntity.Gender outGender;
    private byte[] outPictureData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Optional<PersonEntity> userOpt = userLoadPort.loadUserByUserId(userId);
        if (userOpt.isPresent()) {
            PersonEntity user = userOpt.get();

            txtUserFirstName.setText(user.getFirstName());
            txtUserLastName.setText(user.getLastName());
            txtUserPhone.setText(user.getPhone());
            if (user.getGender() == PersonEntity.Gender.MALE) {
                cmbUserGender.getSelectionModel().selectFirst();
            } else {
                cmbUserGender.getSelectionModel().selectLast();
            }
        } else {
            close();
        }
    }

    @FXML
    void onClose(MouseEvent event) {
        close();
    }

    @FXML
    void onSave(MouseEvent event) throws IOException {
        String firstNameStr = getOr(txtUserFirstName, "");
        String lastNameStr = getOr(txtUserLastName, "");
        String phoneStr = getOr(txtUserPhone, "");
        String genderStr = getOr(cmbUserGender, "");

        if (validate(firstNameStr, lastNameStr, phoneStr, genderStr)) {
            PersonEntity.Gender gender = genderStr.equals("Muški") ? PersonEntity.Gender.MALE : PersonEntity.Gender.FEMALE;
            userUpdatePort.update(userId, firstNameStr, lastNameStr, gender, phoneStr, outPictureData);
            close();
        }
    }

    boolean validate(String firstNameStr, String lastNameStr, String phoneStr, String genderStr) {
        ValidatorResponse verify = userUpdatePort.verify(firstNameStr, lastNameStr, genderStr, phoneStr);
        if (verify.hasErrors()) {
            lblErrUserFirstName.setText(verify.getErrorOrEmpty("firstName"));
            lblErrUserLastName.setText(verify.getErrorOrEmpty("lastName"));
            lblErrUserPhone.setText(verify.getErrorOrEmpty("phone"));
            lblErrUserGender.setText(verify.getErrorOrEmpty("gender"));

            return false;
        }

        return true;
    }

    private void close() {
        root.getScene().getWindow().hide();
    }

    @FXML
    void onCmbGenderAction(ActionEvent event) {
        lblErrUserGender.setText("");
    }

    @FXML
    void txtUserFirstNameReset(KeyEvent event) {
        lblErrUserFirstName.setText("");
    }

    @FXML
    void txtUserLastNameReset(KeyEvent event) {
        lblErrUserLastName.setText("");
    }

    @FXML
    void txtUserPhoneReset(KeyEvent event) {
        lblErrUserPhone.setText("");
    }

    @FXML
    private void onAddPicture() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Stack Trace");
        File file = fileChooser.showOpenDialog(null);
        if (file != null && file.exists()) {
            lblErrPicture.setText("");

            boolean isImage = file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg");
            if (!isImage) {
                lblErrPicture.setText("Mogu samo jpg i png slike.");
                return;
            }
            outPictureData = Files.readAllBytes(file.toPath());
        }
    }
}
