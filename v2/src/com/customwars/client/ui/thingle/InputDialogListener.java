package com.customwars.client.ui.thingle;

/**
 * Description of a class responding to events occurring within the input dialog. The
 * implementation is notified when a button has been clicked on.
 */
public interface InputDialogListener {
	/**
	 * Notification that a given button was clicked on
	 *
	 * @param source The dialog that we are listening to
     * @param button The button that was clicked on
	 */
	public void buttonClicked(ThingleInputDialog source, DialogResult button);
}
