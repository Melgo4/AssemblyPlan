package assembly;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Assembly extends Application {

	private Pane pane = new Pane();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Workbook workbook = new Workbook("test.xlsx");
		Sheet sheet = workbook.getSheet("Orchestra");

		Scene scene = new Scene(this.pane, 1280, 720);
		stage.setScene(scene);

		sheet.render(this.pane, stage);

		scene.heightProperty().addListener(e -> Platform.runLater(() -> sheet.render(this.pane, stage)));
		scene.widthProperty().addListener(e -> Platform.runLater(() -> sheet.render(this.pane, stage)));
		stage.show();
	}

}
