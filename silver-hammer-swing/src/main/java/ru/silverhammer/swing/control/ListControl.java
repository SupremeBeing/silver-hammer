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

import ru.silverhammer.core.collection.ICollection;
import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.ISelectionControl;

import javax.swing.*;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

// TODO: disable internal first key navigation
public abstract class ListControl<A extends Annotation> extends Control<Object, A, JList<Object>>
        implements ICollectionControl<Object, Object, A>, ISelectionControl<Object, Object, A> {

    protected ListControl() {
        super(true);
        getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getComponent().getSelectionModel().addListSelectionListener(e -> fireValueChanged());
        getComponent().addKeyListener(new SearchAdapter() {
            @Override
            protected void search(String search) {
                for (int i = 0; i < getModel().getSize(); i++) {
                    Object item = getModel().getElementAt(i);
                    if (item != null && item.toString().contains(search)) {
                        getComponent().setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
    }

    protected DefaultListModel<Object> getModel() {
        return (DefaultListModel<Object>) getComponent().getModel();
    }

    public int getVisibleRowCount() {
        return getComponent().getVisibleRowCount();
    }

    public void setVisibleRowCount(int count) {
        getComponent().setVisibleRowCount(count);
    }

    public boolean isMultiSelection() {
        int mode = getComponent().getSelectionMode();
        if (mode == ListSelectionModel.SINGLE_SELECTION) {
            return false;
        }
        return true;
    }

    public void setSelectionType(boolean mode) {
        if (mode) {
            getComponent().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    @Override
    public ICollection<Object> getCollection() {
        return new ICollection<Object>() {
            @Override
            public void add(Object item) {
                if (item != null) {
                    getModel().addElement(item);
                    fireValueChanged();
                }
            }

            @Override
            public void remove(int i) {
                getModel().remove(i);
                fireValueChanged();
            }

            @Override
            public int getCount() {
                return getModel().getSize();
            }

            @Override
            public Object get(int i) {
                return getModel().get(i);
            }

            @Override
            public void clear() {
                getModel().removeAllElements();
                fireValueChanged();
            }
        };
    }

    @Override
    public ICollection<Object> getSelection() {
        return new ICollection<Object>() {
            @Override
            public void add(Object item) {
                int count = getModel().getSize();
                for (int i = 0; i < count; i++) {
                    Object val = getModel().get(i);
                    if (Objects.equals(item, val)) {
                        getComponent().setSelectionInterval(i, i);
                        break;
                    }
                }
            }

            @Override
            public void remove(int i) {
                int count = getModel().getSize();
                Object selected = get(i);
                for (int j = 0; j < count; j++) {
                    Object val = getModel().get(i);
                    if (Objects.equals(selected, val)) {
                        getComponent().removeSelectionInterval(j, j);
                        break;
                    }
                }
            }

            @Override
            public int getCount() {
                Collection<Object> list = getComponent().getSelectedValuesList();
                return list.size();
            }

            @Override
            public Object get(int i) {
                List<Object> list = getComponent().getSelectedValuesList();
                return list.get(i);
            }

            @Override
            public void clear() {
                getComponent().clearSelection();
            }
        };
    }

    @Override
    protected JList<Object> createComponent() {
        return new JList<>(new DefaultListModel<>());
    }

}
