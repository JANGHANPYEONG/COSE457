package com.vector.editor.command;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyChangeCommand<T> implements Command {
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final T oldValue;
    private final T newValue;

    public PropertyChangeCommand(Supplier<T> getter, Consumer<T> setter, T oldValue, T newValue) {
        this.getter = getter;
        this.setter = setter;
        this.oldValue = getter.get();
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
