/**
 * created 10.12.2013
 */
package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * @author M. Comp. Sc. Marcus Bleil<br>
 *         www.marcusbleil.de
 */
public class VotePicture extends VBox {

	private CheckBox favorite;
	private String styleUnselected =
		"-fx-border-width: 2; -fx-border-color: #c50e1f; -fx-border-insets: 5; -fx-padding: 7; -fx-background-color: null";
	private String styleSelected =
		"-fx-border-width: 2; -fx-border-color: #c50e1f; -fx-border-insets: 5; -fx-padding: 7; -fx-background-color: gold; -fx-background-insets: 5";

	public VotePicture(Vote vote) {
		Image image;
		favorite = new CheckBox(vote.getTitel());
		favorite.setStyle("-fx-font-size: 12");

		try {
			image =
				new Image(Files.newInputStream(Paths.get(Voter.PICTURE_FOLDER, vote.getFileName())),
					165, 123.75, true,
					true);
			ImageView view = new ImageView(image);

			favorite.setGraphic(view);
			favorite.setContentDisplay(ContentDisplay.TOP);

			favorite.selectedProperty().bindBidirectional(vote.selectedProperty());

			favorite.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
					Boolean newValue) {
					if (newValue.booleanValue()) {
						VotePicture.this.setStyle(styleSelected);
					}
					else {
						VotePicture.this.setStyle(styleUnselected);
					}
				}
			});
			getChildren().add(favorite);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.setStyle(styleUnselected);
	}

	public void setEventHandlerForVote(EventHandler<ActionEvent> eventHandler) {
		favorite.setOnAction(eventHandler);
	}
}
