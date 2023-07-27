package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.entity.BarcodeEntity;
import com.example.gambarucmsui.adapter.out.persistence.repo.BarcodeRepository;
import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import com.example.gambarucmsui.util.generators.BarcodeGenerator;
import com.example.gambarucmsui.util.generators.BarcodeView;
import com.example.gambarucmsui.util.generators.PDFGenerator;
import com.google.zxing.WriterException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class PanelBarcodeController implements PanelHeader {
    private final BarcodeRepository barcodeRepository;

    public PanelBarcodeController(Stage primaryStage, HashMap<Class, Repository> repositoryMap) {
        this.barcodeRepository = (BarcodeRepository) repositoryMap.get(BarcodeRepository.class);
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
        List<BarcodeEntity> barcodeEntities = barcodeRepository.fetchOrGenerateBarcodes(100, BarcodeEntity.Status.NOT_USED);
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
        List<BarcodeEntity> barcodeEntities = barcodeRepository.generateNewBarcodes(100);
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
        List<BarcodeEntity> barcodeId = barcodeRepository.findByIds("barcodeId", collect);

        for (BarcodeEntity barcodeEntity : barcodeId) {
            BarcodeView barcodeView = BarcodeGenerator.generateBarcodeImage(barcodeEntity.getBarcodeId(), 300, 100);
            views.add(barcodeView);
        }


        byte[] bytes = PDFGenerator.generatePDF(views);
        Files.write(Path.of(file.getAbsolutePath()), bytes, StandardOpenOption.CREATE);
    }
}
