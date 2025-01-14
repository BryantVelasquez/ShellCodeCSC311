package org.example.javafxdb_sql_shellcode;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.util.List;

public class UserInterface extends Application {
    private ConnDbOps connDbOps;
    private ListView<String> userListView;

    //boolean to keep track of light or dark modes
    private boolean isLightTheme = true;

    public UserInterface(ConnDbOps connDbOps) {
        this.connDbOps = connDbOps;
    }
// creates UI and event handlers
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management");
        applyTheme(primaryStage);
//creates a menu bar with a file menu and theme switch option
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem switchThemeItem = new MenuItem("Switch Theme");
        switchThemeItem.setOnAction(e -> switchTheme(primaryStage)); //switches theme
        menuFile.getItems().add(switchThemeItem);
        menuBar.getMenus().add(menuFile);
//input fields for user data
        // and buttons for inserting, editing, and deleting users
        TextField nameTextField = new TextField();
        TextField emailTextField = new TextField();
        TextField phoneTextField = new TextField();
        TextField addressTextField = new TextField();
        PasswordField passwordTextField = new PasswordField();
        Button insertButton = new Button("Insert User");
        Button editButton = new Button("Edit User");
        Button deleteButton = new Button("Delete User");
    //displays users
        userListView = new ListView<>();
        refreshUserList();

        insertButton.setOnAction(event -> {
            String name = nameTextField.getText();
            String email = emailTextField.getText();
            String phone = phoneTextField.getText();
            String address = addressTextField.getText();
            String password = passwordTextField.getText();
            connDbOps.insertUser(name, email, phone, address, password);
            refreshUserList();
        });

        editButton.setOnAction(event -> {
            int id = getSelectedUserId();
            if (id != -1) {
                String name = nameTextField.getText();
                String email = emailTextField.getText();
                String phone = phoneTextField.getText();
                String address = addressTextField.getText();
                String password = passwordTextField.getText();
                connDbOps.updateUser(id, name, email, phone, address, password);
                refreshUserList();
            } else {
                showAlert("Please select a user to edit.");
            }
        });

        deleteButton.setOnAction(event -> {
            int id = getSelectedUserId();
            if (id != -1) {
                connDbOps.deleteUser(id);
                refreshUserList();
            } else {
                showAlert("Please select a user to delete.");
            }
        });


        userListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                String[] parts = newValue.split(": ");
                int id = Integer.parseInt(parts[0].trim());
                nameTextField.setText(parts[1].trim());
            }
        });


        VBox layout = new VBox(10, menuBar, userListView, nameTextField, emailTextField, phoneTextField, addressTextField, passwordTextField, insertButton, editButton, deleteButton);
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    // swithces between light and dark theme
    private void switchTheme(Stage primaryStage) {
        isLightTheme = !isLightTheme;
        applyTheme(primaryStage);
    }
    //applies current theme
    private void applyTheme(Stage primaryStage) {
        String theme = isLightTheme ? "light-theme.css" : "dark-theme.css";
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource("/" + theme).toExternalForm());
    }
    //gets ID from the selected user
    private int getSelectedUserId() {
        String selected = userListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String[] parts = selected.split(": ");
            return Integer.parseInt(parts[0].trim());
        }
        return -1;
    }
    //Refreshes user list
    private void refreshUserList() {
        userListView.getItems().clear();
        List<String> users = connDbOps.getAllUsers();
        userListView.getItems().addAll(users);
    }
// displays alert
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
