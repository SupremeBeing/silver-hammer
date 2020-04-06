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

import ru.silverhammer.control.ICollectionControl;
import ru.silverhammer.control.ISelectionControl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TableControl<A extends Annotation> extends Control<Object, A, JTable>
        implements ICollectionControl<Object, A, Object[]>, ISelectionControl<Object, A, Object[]> {

    protected class TableModel extends AbstractTableModel {

        private static final long serialVersionUID = 254186348731104319L;

        @Override
        public String getColumnName(int column) {
            return captions != null && column < captions.size() ? captions.get(column) : null;
        }

        @Override
        public int getColumnCount() {
            return captions == null ? 0 : captions.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (data != null && rowIndex < data.size()) {
                Object[] row = data.get(rowIndex);
                if (columnIndex < row.length) {
                    return row[columnIndex];
                }
            }
            return null;
        }

        @Override
        public int getRowCount() {
            return data == null ? 0 : data.size();
        }
    }

    private final List<String> captions = new ArrayList<>();
    protected final ArrayList<Object[]> data = new ArrayList<>();

    protected TableControl() {
        super(true);
        getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getComponent().getSelectionModel().addListSelectionListener(e -> fireValueChanged());
        getComponent().addKeyListener(new SearchAdapter() {
            @Override
            protected void search(String search) {
                for (int i = 0; i < data.size(); i++) {
                    for (int j = 0; j < captions.size(); j++) {
                        Object val = getModel().getValueAt(i, j);
                        if (val != null && val.toString().contains(search)) {
                            getComponent().setRowSelectionInterval(i, i);
                            break;
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected TableModel getModel() {
        return (TableModel) getComponent().getModel();
    }

    public int getVisibleRowCount() {
        Dimension size = getComponent().getPreferredScrollableViewportSize();
        return (int) (size.getHeight() / getComponent().getRowHeight());
    }

    public void setVisibleRowCount(int count) {
        Dimension size = new Dimension((int) getComponent().getPreferredScrollableViewportSize().getWidth(), count * getComponent().getRowHeight());
        getComponent().setPreferredScrollableViewportSize(size);
    }

    public boolean isMultiSelection() {
        int mode = getComponent().getSelectionModel().getSelectionMode();
        return mode != ListSelectionModel.SINGLE_SELECTION;
    }

    public void setSelectionType(boolean mode) {
        if (mode) {
            getComponent().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    @Override
    public List<Object[]> getSelection() {
        return new ArrayList<Object[]>() {
            @Override
            public boolean add(Object[] item) {
                int i = findRow(item);
                if (i != -1) {
                    getComponent().setRowSelectionInterval(i, i);
                    return true;
                }
                return false;
            }

            @Override
            public Object[] remove(int i) {
                int j = findRow(get(i));
                if (j != -1) {
                    getComponent().removeRowSelectionInterval(j, j);
                }
                return null;
            }

            @Override
            public int size() {
                return getComponent().getSelectedRows().length;
            }

            @Override
            public Object[] get(int i) {
                int index = getComponent().getSelectedRows()[i];
                return data.get(index);
            }

            @Override
            public void clear() {
                getComponent().clearSelection();
            }
        };
    }

    public List<String> getCaptions() {
        return new ArrayList<String>() {
            @Override
            public boolean add(String caption) {
                captions.add(caption);
                getModel().fireTableStructureChanged();
                return true;
            }

            @Override
            public String remove(int i) {
                captions.remove(i);
                getModel().fireTableStructureChanged();
                return null;
            }

            @Override
            public int size() {
                return captions.size();
            }

            @Override
            public String get(int i) {
                return captions.get(i);
            }

            @Override
            public void clear() {
                captions.clear();
                getModel().fireTableStructureChanged();
            }
        };
    }

    @Override
    protected JTable createComponent() {
        return new JTable(new TableModel());
    }

    @Override
    public List<Object[]> getCollection() {
        return new ArrayList<Object[]>() {
            @Override
            public boolean add(Object[] item) {
                if (item != null) {
                    data.add(item);
                    getModel().fireTableDataChanged();
                    fireValueChanged();
                    return true;
                }
                return false;
            }

            @Override
            public Object[] remove(int i) {
                data.remove(i);
                getModel().fireTableDataChanged();
                fireValueChanged();
                return null;
            }

            @Override
            public int size() {
                return data.size();
            }

            @Override
            public Object[] get(int i) {
                return data.get(i);
            }

            @Override
            public void clear() {
                data.clear();
                getModel().fireTableDataChanged();
                fireValueChanged();
            }
        };
    }

    protected int findRow(Object[] value) {
        for (int i = 0; i < data.size(); i++) {
            if (Arrays.equals(data.get(i), value)) {
                return i;
            }
        }
        return -1;
    }
}
