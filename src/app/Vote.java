/**
 * created 10.12.2013
 */
package app;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

/**
 * @author M. Comp. Sc. Marcus Bleil<br>
 *         www.marcusbleil.de
 */
@Root(name = "vote")
public class Vote implements Comparable<Vote> {

	@Attribute
	private String fileName;
	@Attribute
	private String title;
	@Attribute
	private int votes;

	@Transient
	private Property<Boolean> selectedProperty = new SimpleBooleanProperty(false);

	protected Vote() {

	}

	public Vote(String fileName) {
		this.fileName = fileName;
		this.title = fileName.substring(0, fileName.length() - 4);
	}

	@Override
	public int compareTo(Vote o) {
		return -Integer.compare(votes, o.votes);
	}

	public String getFileName() {
		return fileName;
	}

	public String getTitel() {
		return title;
	}

	public Property<Boolean> selectedProperty() {
		return selectedProperty;
	}

	public void storeVote() {
		if (selectedProperty.getValue().booleanValue()) {
			votes++;
			selectedProperty.setValue(Boolean.FALSE);
		}
	}
}
