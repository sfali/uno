package com.alphasystem.game.uno.client.ui.control.delegate;

import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public final class PlayerView extends Control {

    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final IntegerProperty points = new SimpleIntegerProperty(this, "points", -1);
    private final IntegerProperty numberOfCardsLeft = new SimpleIntegerProperty(this, "numberOfCardsLeft", 0);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);

    public PlayerView() {
        setSkin(createDefaultSkin());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PlayerSkin(this);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getPoints() {
        return points.get();
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public void setPoints(int points) {
        this.points.set(points);
    }

    public int getNumberOfCardsLeft() {
        return numberOfCardsLeft.get();
    }

    public IntegerProperty numberOfCardsLeftProperty() {
        return numberOfCardsLeft;
    }

    public void setNumberOfCardsLeft(int numberOfCardsLeft) {
        this.numberOfCardsLeft.set(numberOfCardsLeft);
    }

    public boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }
}
