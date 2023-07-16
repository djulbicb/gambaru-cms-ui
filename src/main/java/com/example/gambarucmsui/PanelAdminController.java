package com.example.gambarucmsui;

import com.example.gambarucmsui.adapter.out.persistence.repo.Repository;
import javafx.fxml.FXML;

import java.util.HashMap;

public class PanelAdminController {
    public PanelAdminController(HashMap<Class, Repository> repositoryMap) {
    }

    @FXML
    private void initialize() {
        System.out.println("membership");
    }



}
