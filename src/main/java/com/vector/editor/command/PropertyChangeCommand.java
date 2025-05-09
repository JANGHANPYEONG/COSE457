package com.vector.editor.command;

import java.util.function.Consumer;

public class PropertyChangeCommand<T> implements Command {
    private final Consumer<T> setter;
    private final T oldValue;
    private final T newValue;

    public PropertyChangeCommand(Consumer<T> setter, T oldValue, T newValue) {
        this.setter = setter;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void execute() {
        setter.accept(newValue);
    }

    @Override
    public void undo() {
        setter.accept(oldValue);
    }
}
