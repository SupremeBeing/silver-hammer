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
package ru.silverhammer.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import ru.silverhammer.core.HorizontalAlignment;
import ru.silverhammer.core.IUiBuilder;
import ru.silverhammer.core.Location;
import ru.silverhammer.core.VerticalAlignment;
import ru.silverhammer.core.metadata.CategoryAttributes;
import ru.silverhammer.core.metadata.ControlAttributes;
import ru.silverhammer.core.metadata.GroupAttributes;
import ru.silverhammer.core.metadata.UiMetadata;

public class SwingUiBuilder implements IUiBuilder<Container> {

	private int borderSize = 10;
	private int controlSpacing = 3;

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public int getControlSpacing() {
		return controlSpacing;
	}

	public void setControlSpacing(int controlSpacing) {
		this.controlSpacing = controlSpacing;
	}
	
	@Override
	public Container buildUi(UiMetadata metadata) {
		if (metadata.hasCategories()) {
			JTabbedPane result = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
			for (CategoryAttributes ca : metadata.getCategories()) {
				if (!ca.isEmpty()) {
					JPanel panel = createPanel();
					URL url = getClass().getResource(ca.getIconPath());
					Icon icon = url == null ? null : new ImageIcon(url); 
					result.addTab(ca.getCaption(), icon, panel, ca.getDescription());
					if (ca.getMnemonic() != 0) {
						int i = result.getTabCount() - 1;
						result.setMnemonicAt(i, Character.toUpperCase(ca.getMnemonic()));
					}
					createGroups(ca, panel);
				}
			}
			return result;
		} else {
			JPanel result = createPanel();
			createGroups(metadata.getGroups(), result);
			return result;
		}
	}

	private void createGroups(Iterable<GroupAttributes> groups, Container groupsContainer) {
		for (GroupAttributes ga : groups) {
			if (!ga.isEmpty()) {
				JPanel groupPanel = new JPanel(new GridBagLayout());
				if (ga.getCaption() != null && ga.getCaption().trim().length() > 0) {
					Border border = BorderFactory.createEtchedBorder();
					groupPanel.setBorder(BorderFactory.createTitledBorder(border, ga.getCaption()));
				}
				groupsContainer.add(groupPanel, createGroupConstraints());
				for (ControlAttributes ca : ga) {
					placeControl(groupPanel, ca);
				}
			}
		}
		GridBagConstraints gbc = createGroupConstraints();
		gbc.weighty = 1;
		groupsContainer.add(new JPanel(new GridBagLayout()), gbc);
	}

	private JPanel createPanel() {
		JPanel result = new JPanel(new GridBagLayout());
		result.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
		return result;
	}
	
	private void placeControl(Container parent, ControlAttributes attributes) {
		Component c = (Component) attributes.getControl();
		if (attributes.getCaption() != null) {
			JLabel label = new JLabel(attributes.getCaption());
			if (attributes.getHorizontalAlignment() == HorizontalAlignment.Center) {
				label.setHorizontalAlignment(SwingConstants.CENTER);
			} else if (attributes.getHorizontalAlignment() == HorizontalAlignment.Right) {
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			} else if (attributes.getHorizontalAlignment() == HorizontalAlignment.Left) {
				label.setHorizontalAlignment(SwingConstants.LEFT);
			}
			if (attributes.getVerticalAlignment() == VerticalAlignment.Bottom) {
				label.setVerticalAlignment(SwingConstants.BOTTOM);
			} else if (attributes.getVerticalAlignment() == VerticalAlignment.Center) {
				label.setVerticalAlignment(SwingConstants.CENTER);
			} else if (attributes.getVerticalAlignment() == VerticalAlignment.Top) {
				label.setVerticalAlignment(SwingConstants.TOP);
			}
			label.setToolTipText(attributes.getDescription());
			if (attributes.getCaptionLocation() == Location.Right) {
				parent.add(c, createSingleConstraints(0));
				parent.add(label, createLabelConstraints(1));
			} else if (attributes.getCaptionLocation() == Location.Top) {
				parent.add(label, createDoubleConstraints());
				parent.add(c, createDoubleConstraints());
			} else if (attributes.getCaptionLocation() == Location.Bottom) {
				parent.add(c, createDoubleConstraints());
				parent.add(label, createDoubleConstraints());
			} else if (attributes.getCaptionLocation() == Location.Left) {
				parent.add(label, createLabelConstraints(0));
				parent.add(c, createSingleConstraints(1));
			}
		} else {
			parent.add(c, createDoubleConstraints());
		}
	}

	private GridBagConstraints createLabelConstraints(int x) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(controlSpacing, controlSpacing, controlSpacing, controlSpacing);
		gbc.gridwidth = 1;
		return gbc;
	}

	private GridBagConstraints createSingleConstraints(int x) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(controlSpacing, controlSpacing, controlSpacing, controlSpacing);
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		return gbc;
	}

	private GridBagConstraints createDoubleConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(controlSpacing, controlSpacing, controlSpacing, controlSpacing);
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		return gbc;
	}
	
	private GridBagConstraints createGroupConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		return gbc;
	}
}
