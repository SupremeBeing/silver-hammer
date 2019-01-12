/*
 * Copyright (c) 2019, Dmitriy Shchekotin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package ru.silverhammer.swing.control;

import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.ISelectionControl;

import javax.swing.*;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ButtonGroupControl<A extends Annotation> extends Control<Object, A, JPanel>
        implements ICollectionControl<Object, Object, A>, ISelectionControl<Object, Object, A> {

    protected final List<Object> data = new ArrayList<>();
    private final Map<Object, AbstractButton> buttons = new HashMap<>();

    protected ButtonGroupControl() {
        super(false);
        setNormalBackground(createButton().getBackground());
    }

    protected abstract AbstractButton createButton();

    protected abstract AbstractButton createButton(Object item);

    @Override
    protected final JPanel createComponent() {
        return new JPanel(new GridLayout(0, 1, 0, 0));
    }

    @Override
    public final void init(A annotation) {}

    @Override
    public int getSelectionCount() {
        List<Object> result = new ArrayList<>();
        for (Object item : data) {
            AbstractButton btn = getButton(item);
            if (btn.isSelected()) {
                result.add(item);
            }
        }
        return result.size();
    }

    @Override
    public Object getSelectedItem(int i) {
        List<Object> result = new ArrayList<>();
        for (Object item : data) {
            AbstractButton btn = getButton(item);
            if (btn.isSelected()) {
                result.add(item);
            }
        }
        return result.get(i);
    }

    @Override
    public void clearSelection() {
        for (Object item : data) {
            AbstractButton btn = getButton(item);
            if (btn.isSelected()) {
                btn.setSelected(false);
            }
        }
    }

    @Override
    public void select(Object value) {
        AbstractButton button = getButton(value);
        if (button != null && !button.isSelected()) {
            button.setSelected(true);
            fireValueChanged();
        }
    }

    @Override
    public void deselect(Object value) {
        AbstractButton button = getButton(value);
        if (button != null && button.isSelected()) {
            button.setSelected(false);
            fireValueChanged();
        }
    }

    @Override
    public void setValidationMessage(String message) {
        for (AbstractButton c : buttons.values()) {
            setValidationMessage(c, message);
        }
    }

    protected AbstractButton getButton(Object item) {
        return buttons.get(item);
    }

    @Override
    public void setItem(int i, Object item) {
        if (item != null) {
            Object oldItem = data.get(i);
            AbstractButton button = buttons.get(oldItem);
            button.setText(item.toString());
            data.set(i, item);
            buttons.remove(oldItem);
            buttons.put(item, button);
            if (button.isSelected()) {
                fireValueChanged();
            }
        }
    }

    @Override
    public void clearItems() {
        getComponent().removeAll();
        buttons.clear();
        data.clear();
        fireValueChanged();
    }

    @Override
    public void addItem(Object item) {
        if (item != null) {
            AbstractButton button = createButton(item);
            buttons.put(item, button);
            data.add(item);
            getComponent().add(button);
        }
    }

    @Override
    public void removeItem(Object item) {
        AbstractButton button = getButton(item);
        if (button != null) {
            getComponent().remove(button);
            buttons.remove(item);
            data.remove(item);
            if (button.isSelected()) {
                fireValueChanged();
            }
        }
    }

    @Override
    public void addItem(int i, Object item) {
        if (item != null) {
            data.add(i, item);
            rebuild();
        }
    }

    @Override
    public void removeItem(int i) {
        data.remove(i);
        rebuild();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    private void rebuild() {
        getComponent().removeAll();
        for (Object o : data) {
            AbstractButton button = createButton(o);
            getComponent().add(button);
            button.setSelected(buttons.get(o) != null && buttons.get(o).isSelected());
            buttons.put(o, button);
        }
        fireValueChanged();
    }
}
