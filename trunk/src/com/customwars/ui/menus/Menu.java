package com.customwars.ui.menus;

import com.customwars.sfx.SFX;

import java.awt.*;

/**
 * A Menu where you can change the selected item by invoking either:
 * menuMoveUp, menuMoveDown or setMenuPosition
 * each function cannot move out of bounds instead it will:
 * set current menu item to max when attempting to go lower the 0 and
 * set current menu item to 0 when trying to go over numMenuItems
 * <p/>
 * <p/>A menuTick sound is played when we changed position
 * withinBounds(int item, int max) can be used by the subclass for bound checks.
 *
 * @author stefan
 * @since 2.0
 */
public abstract class Menu {
    private int numMenuItems;
    private int currentMenuItem;

    protected Menu(int numMenuItems) {
        this.numMenuItems = numMenuItems;
    }

    void menuMoveUp() {
        setCurrentMenuItem(--currentMenuItem);
    }

    void menuMoveDown() {
        setCurrentMenuItem(++currentMenuItem);
    }

    void setCurrentMenuItem(int item) {
        currentMenuItem = withinBounds(item, numMenuItems - 1);
        playMenuTick();
    }

    /**
     * @param item the new item to validate against the menu item bounds
     * @param max  exclusive max bound
     *             (item=2 max=2) will return 2,
     *             (item=3 max=2) will return 0
     * @return max if the item was <0
     *         0 if the item was > max
     *         item if the item was within bounds
     */
    final int withinBounds(int item, int max) {
        if (item < 0) {
            return max;
        } else if (item > max) {
            return 0;
        } else {
            return item;
        }
    }

    int getCurrentMenuItem() {
        return currentMenuItem;
    }

    void playMenuTick() {
        SFX.playSound("menutick.wav");
    }

    abstract void paintMenu(Graphics2D g);
}
