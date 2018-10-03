package app;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * @author M. Comp. Sc. Marcus Bleil<br>
 *         www.marcusbleil.de
 */
public class Voter extends Application implements EventHandler<ActionEvent> {

	@Root(name = "vote-list")
	static class VoteList {

		@ElementList(inline = true)
		List<Vote> votes = new ArrayList<>();

		public void updateFileList(String pictureFolder) {
			List<Vote> votesForRemove;

			votesForRemove = new ArrayList<>();

			for (Vote vote : votes) {
				Path picturePath;
				picturePath = Paths.get(pictureFolder, vote.getFileName());

				if (!Files.exists(picturePath)) {
					votesForRemove.add(vote);
					System.out.println(picturePath.getFileName().toString() + " removed");
				}
			}

			votes.removeAll(votesForRemove);
		}

		public boolean contains(String fileName) {
			boolean containing = false;

			for (Vote vote : votes) {
				if (vote.getFileName().equals(fileName)) {
					containing = true;
				}
			}
			return containing;
		}

		/**
		 * @return
		 */
		public int countVoted() {
			int voted = 0;

			for (Vote vote : votes) {
				if (vote.selectedProperty().getValue()) {
					voted++;
				}
			}
			return voted;
		}
	}

	public final static String PICTURE_FOLDER = "pictures";

	public final static String VOTINGS_FILE = "votings.xml";

	public static void main(String[] args) {
		launch(args);
	}

	private Serializer serializer = new Persister();
	private VoteList votesList = new VoteList();
	private Label message = new Label();

	@Override
	public void start(Stage primaryStage) {

		loadVoteList();

		removeVotesForMissingFiles();

		updatePicturesForVoting();

		BorderPane root = new BorderPane();

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(buildVoteObjectsView());
		root.setCenter(scrollPane);
		root.setBottom(buildVoteButton());

		primaryStage.setScene(new Scene(root, 1024, 786));
		primaryStage.setMaximized(true);
		primaryStage.setTitle("Kochwettbewerb");
		primaryStage.show();
	}

	private Node buildVoteButton() {
		HBox box = new HBox();

		Button voteButton = new Button("Abstimmen");
		voteButton.setStyle("-fx-font-size: 20; -fx-label-padding: 7");

		voteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				int countVoted;

				countVoted = votesList.countVoted();

				if (countVoted <= 3) {
					for (Vote vote : votesList.votes) {
						vote.storeVote();
					}
					try {
						// serializer.write(votesList, System.out);
						// System.out.println();

						Path voteFile = Paths.get(VOTINGS_FILE);
						Collections.sort(votesList.votes);
						serializer.write(votesList, voteFile.toFile());

						message.setText("Abstimmung wurde gespeichert, Danke");
					}
					catch (Exception e) {

						// TODO verbessern
						e.printStackTrace();
						System.exit(1);
					}
				}
				else {
					message
						.setText("es sind nur 3 Gerichte erlaubt, daher wird die Abstimmung nicht gespeichert");
				}
			}

		});

		box.getChildren().add(voteButton);
		message.setStyle("-fx-font-size: 20; -fx-label-padding: 7");
		box.getChildren().add(message);

		return box;
	}

	private Pane buildVoteObjectsView() {
		TilePane voteObjects;

		voteObjects = new TilePane();
		voteObjects.setPrefColumns(4);

		Collections.shuffle(votesList.votes);

		for (Vote v : votesList.votes) {
			VotePicture voteControl;

			voteControl = new VotePicture(v);
			voteObjects.getChildren().add(voteControl);

			voteControl.setEventHandlerForVote(this);
		}

		return voteObjects;
	}

	private void loadVoteList() {
		Path votingFile;

		votingFile = Paths.get(VOTINGS_FILE);

		if (!Files.isRegularFile(votingFile)) {
			votesList = new VoteList();
		}
		else {
			try {
				votesList = serializer.read(VoteList.class, votingFile.toFile());
			}
			catch (Exception e) {
				// TODO noch eine MessageBox
				e.printStackTrace();
			}
		}
	}

	private void removeVotesForMissingFiles() {
		votesList.updateFileList(PICTURE_FOLDER);
	}

	private void updatePicturesForVoting() {

		try (DirectoryStream<Path> files = Files.newDirectoryStream(Paths.get(PICTURE_FOLDER))) {

			for (Path path : files) {

				if (!votesList.contains(path.getFileName().toString())) {

					Vote vote = new Vote(path.getFileName().toString());
					votesList.votes.add(vote);

					System.out.println(path.getFileName().toString() + " neu geladen");
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javafx.event.EventHandler#handle(javafx.event.Event)
	 */
	@Override
	public void handle(ActionEvent event) {
		int selected;

		selected = votesList.countVoted();

		if (selected <= 3) {
			message
				.setText(selected + " Gericht(e) ausgewählt - erlaubt sind höchstens 3 Gerichte");
		}
		else {
			message.setText(selected + " Gericht(e): das sind zu viele Gerichte, bitte "
				+ (selected - 3)
				+ "x abwählen");
		}

	}
}
