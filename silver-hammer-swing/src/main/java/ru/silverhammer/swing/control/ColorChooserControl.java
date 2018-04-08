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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;

public class ColorChooserControl extends ValidatableControl<Color, JPanel> {
	
	private static final long serialVersionUID = -3000952254597666980L;

	private Color value;
	
	private JPanel colorPanel;
	private JSlider red;
	private JSlider green;
	private JSlider blue;
	private JSlider alpha;

	public ColorChooserControl() {
		super(false);

		colorPanel = new JPanel() {
			private static final long serialVersionUID = 4174692140273993404L;

//			@Override
//			public int getWidth() {
//				return getHeight();
//			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(value);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		colorPanel.setBorder(BorderFactory.createEtchedBorder());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 4;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		getComponent().add(colorPanel, gbc);

		JLabel label1 = new JLabel(UIManager.getString("ColorChooser.rgbRedText", getLocale()));
		getComponent().add(label1, createLableConstraints(0));
		red = createSlider();
		getComponent().add(red, createSliderConstraints(0));

		JLabel label2 = new JLabel(UIManager.getString("ColorChooser.rgbGreenText", getLocale()));
		getComponent().add(label2, createLableConstraints(1));
		green = createSlider();
		getComponent().add(green, createSliderConstraints(1));

		JLabel label3 = new JLabel(UIManager.getString("ColorChooser.rgbBlueText", getLocale()));
		getComponent().add(label3, createLableConstraints(2));
		blue = createSlider();
		getComponent().add(blue, createSliderConstraints(2));

		JLabel label4 = new JLabel(UIManager.getString("ColorChooser.rgbAlphaText", getLocale()));
		getComponent().add(label4, createLableConstraints(3));
		alpha = createSlider();
		getComponent().add(alpha, createSliderConstraints(3));
		
		setNormalBackground(red.getBackground());
	}
	
	@Override
	protected JPanel createComponent() {
		return new JPanel(new GridBagLayout());
	}

	@Override
	public void setValidationMessage(String message) {
		super.setValidationMessage(message);
		red.setToolTipText(message);
		red.setBackground(message == null ? getNormalBackground() : getInvalidBackground());
		green.setToolTipText(message);
		green.setBackground(message == null ? getNormalBackground() : getInvalidBackground());
		blue.setToolTipText(message);
		blue.setBackground(message == null ? getNormalBackground() : getInvalidBackground());
		alpha.setToolTipText(message);
		alpha.setBackground(message == null ? getNormalBackground() : getInvalidBackground());
	}

	private GridBagConstraints createLableConstraints(int gridy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 5, gridy == 3 ? 0 : 5, 5);
		gbc.weightx = 0;
		return gbc;
	}
	
	private JSlider createSlider() {
		JSlider result = new JSlider();
		result.setMinimum(0);
		result.setMaximum(255);
		result.setMajorTickSpacing(50);
		result.addChangeListener(l -> updateColor());
		return result;
	}
	
	private GridBagConstraints createSliderConstraints(int gridy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 0, gridy == 3 ? 0 : 5, 0);
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		return gbc;
	}
	
	private void updateColor() {
		int r = red.getValue();
		int g = green.getValue();
		int b = blue.getValue();
		int a = alpha.getValue();
		value = new Color(r, g, b, a);
		fireValueChanged();
		colorPanel.repaint();
	}
	
	@Override
	public Color getValue() {
		return value;
	}

	@Override
	public void setValue(Color value) {
		if (value == null) {
			value = Color.BLACK;
		}
		this.value = value;
		red.setValue(value.getRed());
		green.setValue(value.getGreen());
		blue.setValue(value.getBlue());
		alpha.setValue(value.getAlpha());
	}
	
	@Override
	public boolean isEnabled() {
		return red.isEnabled() && green.isEnabled() && blue.isEnabled() && alpha.isEnabled();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		red.setEnabled(enabled);
		green.setEnabled(enabled);
		blue.setEnabled(enabled);
		alpha.setEnabled(enabled);
	}
}
