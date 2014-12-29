package com.customwars.client.ui.thingle;

/**
 * Description of a class responding to events occurring within the dialog. The
 * implementation is notified when a button has been clicked on.
 */
public interface DialogListener {
	/**
	 * Notification that a given button was clicked on
	 *
	 * @param button The button that was clicked on
	 */
	public void buttonClicked(DialogResult button);
}
