package server;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JLabel;
import server.ChatGUi;

/**
 *
 * @author Taioli Francesco taioli.francesco98@gmail.com classe principale,
 * lancia il login
 */
public class Indixis extends Application {

    private static int cont = 0;
    TextField userTextField = new TextField();
    PasswordField password = new PasswordField();
    final Text actiontarget = new Text();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle(" Indixis");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 500);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Indixis.class.getResource("Indixis.css").toExternalForm());
        primaryStage.setResizable(false);
//        primaryStage.getIcons().add(new Image("icon.svg"));                 // immagine
        Text scenetitle = new Text("Indixis");
        scenetitle.setFont(Font.loadFont("src/Roboto.ttf", 120));
        FadeTransition ft = new FadeTransition(Duration.millis(5000), scenetitle);
        ft.setFromValue(-1.0);
        ft.setToValue(3.0);
        ft.play();

        scenetitle.setId("title");
        grid.add(scenetitle, 0, 0, 2, 1);

        GridPane nomeutente = new GridPane();
        Label userName = new Label("Username");

        nomeutente.add(userTextField, 1, 1);

        nomeutente.add(userName, 1, 0);
        grid.add(nomeutente, 0, 1);

        GridPane passwordGrid = new GridPane();

        Label pw = new Label("Password");
        passwordGrid.add(pw, 1, 0);
        passwordGrid.add(password, 1, 1);

        grid.add(passwordGrid, 0, 2);

        //chekckbox
        HBox ckBox = new HBox(10);
        CheckBox ck = new CheckBox("Show advanced option");
        ckBox.getChildren().add(ck);
        grid.add(ckBox, 0, 6);

        GridPane advanced = new GridPane();

        VBox option = new VBox(5);
        VBox ipBOx = new VBox(2);
        TextField ip = new TextField();
        Label ipText = new Label("Password ");
        ip.setPrefWidth(100);
        ipBOx.getChildren().add(ipText);
        ipBOx.getChildren().add(ip);

        VBox portBOx = new VBox(2);
        TextField port = new TextField();
        Label portTest = new Label("Username ");
        port.setPrefWidth(30);
        portBOx.getChildren().add(portTest);
        portBOx.getChildren().add(port);

        advanced.add(option, 0, 1);

        grid.add(advanced, 0, 9);

        

        Button btn = new Button("Sign in");
        Button register = new Button("Register");
        HBox hbBtn = new HBox(14);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        grid.add(register, 1, 5);

        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");
        btn.setDefaultButton(true);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String usernameLogin = userTextField.getText();
                String passwordLogin = String.valueOf(password.getText());
                System.out.println(usernameLogin + "--" + passwordLogin);
                //ChatGUi graphics = new ChatGUi("giovanni", "123456--"); //senza mettere i nomi in debug
                ChatGUi graphics = new ChatGUi(usernameLogin, passwordLogin); //i mettere le stringe sopra
                graphics.setUser(usernameLogin);
                graphics.setVisible(true);
                actiontarget.setText(" Sto loggando ...");
                Platform.exit();
            }
        });

        register.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                 if (cont % 2 == 0) {
                    option.getChildren().add(portBOx);
                    option.getChildren().add(ipBOx);
                } else {
                    option.getChildren().remove(portBOx);
                    option.getChildren().remove(ipBOx);
                }
                cont++;
            
            }
        });
        
        
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
