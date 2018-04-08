/*
 * Copyright (c) 2017, Dmitriy Shchekotin
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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import ru.silverhammer.core.control.ICollectionControl;
import ru.silverhammer.core.control.IMultiCaptionControl;
import ru.silverhammer.core.control.IRowsControl;
import ru.silverhammer.core.control.ISelectionTypeControl;
import ru.silverhammer.core.control.IValueTypeControl;

public class TableControl extends ValidatableControl<Object, JTable> implements ICollectionControl<Object[], Object>,
	IMultiCaptionControl<Object>, IValueTypeControl<Object>, IRowsControl<Object>, ISelectionTypeControl<Object> {

	private static final long serialVersionUID = -3692427066762483919L;

	private class TableModel extends AbstractTableModel {

		private static final long serialVersionUID = 254186348731104319L;
		
		@Override
		public String getColumnName(int column) {
			return column < captions.size() ? captions.get(column) : null;
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
	private final ArrayList<Object[]> data = new ArrayList<>();

	private ValueType valueType = ValueType.Selection;

	public TableControl() {
		super(true);
		getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getComponent().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (valueType == ValueType.Selection) {
					fireValueChanged();
				}
			}
		});
		getComponent().addKeyListener(new SearchAdapter() {
			@Override
			protected void search(String search) {
				for (int i = 0; i < data.size(); i++) {
					Object item = data.get(i);
					if (item != null) {
						for (int j = 0; j < captions.size(); j++) {
							Object val = getModel().getValueAt(i, j);
							if (val != null && val.toString().contains(search)) {
								getComponent().setRowSelectionInterval(i, i);
								break;
							}
						}
					}
				}
			}
		});
	}
	
	private TableModel getModel() {
		return (TableModel) getComponent().getModel();
	}

	@Override
	public Object getValue() {
		if (getValueType() == ValueType.Selection) {
			if (getSelectionType() == SelectionType.Single) {
				int i = getComponent().getSelectedRow();
				return i == -1 ? null : data.get(i);
			} else {
				List<Object[]> result = new ArrayList<>();
				for (int i : getComponent().getSelectedRows()) {
					result.add(data.get(i));
				}
				return result;
			}
		} else if (getValueType() == ValueType.Content) {
			return (Collection<?>) data.clone();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if (getValueType() == ValueType.Selection) {
			getComponent().clearSelection();
			if (getSelectionType() == SelectionType.Single && value instanceof Object[]) {
				int i = findRow((Object[]) value);
				if (i != -1) {
					getComponent().setRowSelectionInterval(i, i);
				}
			} else if (getSelectionType() == SelectionType.Interval && value instanceof Collection) {
				for (Object[] o : (Collection<Object[]>) value) {
					int i = findRow(o);
					if (i != -1) {
						getComponent().setRowSelectionInterval(i, i);
					}
				}
			}
		} else if (getValueType() == ValueType.Content && value instanceof Collection) {
			data.clear();
			data.addAll((Collection<Object[]>) value);
			getModel().fireTableStructureChanged();
			fireValueChanged();
		}
	}
	
	private int findRow(Object[] value) {
		for (int i = 0; i < data.size(); i++) {
			if (Arrays.equals(data.get(i), value)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ValueType getValueType() {
		return valueType;
	}

	@Override
	public void setValueType(ValueType mode) {
		this.valueType = mode;
	}

	@Override
	public int getVisibleRowCount() {
		Dimension size = getComponent().getPreferredScrollableViewportSize();
		return (int) (size.getHeight() / getComponent().getRowHeight());
	}

	@Override
	public void setVisibleRowCount(int count) {
		Dimension size = new Dimension((int) getComponent().getPreferredScrollableViewportSize().getWidth(), count * getComponent().getRowHeight());
		getComponent().setPreferredScrollableViewportSize(size); 
	}

	@Override
	public SelectionType getSelectionType() {
		int mode = getComponent().getSelectionModel().getSelectionMode();
		if (mode == ListSelectionModel.SINGLE_SELECTION) {
			return SelectionType.Single;
		} else if (mode == ListSelectionModel.SINGLE_INTERVAL_SELECTION) {
			return SelectionType.Interval;
		} else if (mode == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
			return SelectionType.Multi;
		}
		return null;
	}

	@Override
	public void setSelectionType(SelectionType mode) {
		if (mode == SelectionType.Single) {
			getComponent().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		} else if (mode == SelectionType.Interval) {
			getComponent().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		} else if (mode == SelectionType.Multi) {
			getComponent().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
	}

	@Override
	public void addCaption(String caption) {
		captions.add(caption);
		getModel().fireTableStructureChanged();
	}

	@Override
	public void removeCaption(String caption) {
		if (captions.remove(caption)) {
			getModel().fireTableStructureChanged();
		}
	}

	@Override
	public String[] getCaptions() {
		return captions.toArray(new String[captions.size()]);
	}

	@Override
	public void addCaption(int i, String caption) {
		captions.add(i, caption);
		getModel().fireTableStructureChanged();
	}

	@Override
	public void setCaption(int i, String caption) {
		captions.set(i, caption);
		getModel().fireTableStructureChanged();
	}

	@Override
	public void removeCaption(int i) {
		captions.remove(i);
		getModel().fireTableStructureChanged();
	}

	@Override
	public int getCaptionCount() {
		return captions.size();
	}

	@Override
	public String getCaption(int i) {
		return captions.get(i);
	}

	@Override
	public void clearCaptions() {
		captions.clear();
		getModel().fireTableStructureChanged();
	}

	@Override
	protected JTable createComponent() {
		return new JTable(new TableModel());
	}
	
	@Override
	public void addItem(Object[] item) {
		if (item != null) {
			data.add(item);
			getModel().fireTableDataChanged();
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void removeItem(Object[] item) {
		int i = findRow(item);
		if (i != -1) {
			data.remove(i);
			getModel().fireTableDataChanged();
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void clearItems() {
		data.clear();
		getModel().fireTableDataChanged();
		if (valueType == ValueType.Content) {
			fireValueChanged();
		}
	}

	@Override
	public void addItem(int i, Object[] item) {
		if (item != null) {
			data.add(i, item);
			getModel().fireTableDataChanged();
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void setItem(int i, Object[] item) {
		if (item != null) {
			data.set(i, item);
			getModel().fireTableDataChanged();
			if (valueType == ValueType.Content) {
				fireValueChanged();
			}
		}
	}

	@Override
	public void removeItem(int i) {
		data.remove(i);
		getModel().fireTableDataChanged();
		if (valueType == ValueType.Content) {
			fireValueChanged();
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public Object[] getItem(int i) {
		return data.get(i);
	}
}
