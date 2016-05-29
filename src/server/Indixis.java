package server;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.ChatGUi;

/**
 *
 * @author Taioli Francesco taioli.francesco98@gmail.com classe principale,
 * lancia il login
 */
public class Indixis extends Application {

    private static int cont = 0;
    TextField userLogin = new TextField();//login
    PasswordField passwordLogin = new PasswordField();//login

    TextField usernameRegistrazione = new TextField();//registrazione
    PasswordField passwordRegistrazione = new PasswordField();//registrazione
    final Text actiontarget = new Text();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle(" Indixis");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 300, 550);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Indixis.class.getResource("Indixis.css").toExternalForm());
        primaryStage.setResizable(false);

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

        nomeutente.add(userLogin, 1, 1);

        nomeutente.add(userName, 1, 0);
        grid.add(nomeutente, 0, 1);

        GridPane passwordGrid = new GridPane();

        Label pw = new Label("Password");
        passwordGrid.add(pw, 1, 0);
        passwordGrid.add(passwordLogin, 1, 1);

        grid.add(passwordGrid, 0, 2);

        GridPane advanced = new GridPane();

        //register
        VBox option = new VBox(5);
        VBox ipBOx = new VBox(5);

        Label passwordLabel = new Label("Password ");
        passwordRegistrazione.setPrefWidth(100);
        ipBOx.getChildren().add(passwordLabel);
        ipBOx.getChildren().add(passwordRegistrazione);

        VBox portBOx = new VBox(3);

        Label usernameLabel = new Label("Username ");
        usernameRegistrazione.setPrefWidth(30);
        portBOx.getChildren().add(usernameLabel);
        portBOx.getChildren().add(usernameRegistrazione);

        Button register = new Button("Register");
        register.setId("reg");
        ipBOx.getChildren().add(register);
        register.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String usernameReg = usernameRegistrazione.getText();
                String passwordReg = passwordRegistrazione.getText();
                System.out.println(usernameReg + " " + passwordReg);
                ChatGUi graphics = new ChatGUi(usernameReg, passwordReg, "registrazione"); //i mettere le stringe sopra
                graphics.setUser(usernameReg);
                actiontarget.setText(" Sto registrando ...");
                graphics.setVisible(true);
                Platform.exit();
            }
        });

        advanced.add(option, 0, 1);

        grid.add(advanced, 0, 10);

        //bottoni register and sign in
        Button login = new Button("Sign in");
        Button openRegister = new Button("Register");
        HBox hbBtn = new HBox(14);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(login);
        grid.add(hbBtn, 1, 4);
        grid.add(openRegister, 1, 5);

        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");
        login.setDefaultButton(true);
        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String usernameLogin = userLogin.getText();
                String passLogin = passwordLogin.getText();
                System.out.println(usernameLogin + "--" + passLogin);
                //ChatGUi graphics = new ChatGUi("giovanni", "123456--"); //senza mettere i nomi in debug

                //ChatGUi graphics = new ChatGUi(usernameLogin, passwordLogin, "login"); //i mettere le stringe sopra
                //graphics.setUser(usernameLogin);
                ChatGUi graphics = new ChatGUi(usernameLogin, passLogin, "login"); //i mettere le stringe sopra
                graphics.setUser(usernameLogin);
                graphics.setVisible(true);
                actiontarget.setText(" Sto facendo il login ...");
                Platform.exit();
            }
        });

        //apre i textfield per la registrazione
        openRegister.setOnAction(new EventHandler<ActionEvent>() {

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
