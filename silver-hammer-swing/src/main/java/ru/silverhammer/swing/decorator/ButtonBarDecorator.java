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
package ru.silverhammer.swing.decorator;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ru.silverhammer.core.HorizontalAlignment;
import ru.silverhammer.core.Location;
import ru.silverhammer.core.VerticalAlignment;
import ru.silverhammer.core.control.IControl;
import ru.silverhammer.core.decorator.IDecorator;
import ru.silverhammer.core.decorator.annotation.ButtonBar;
import ru.silverhammer.core.decorator.annotation.ButtonBar.Button;
import ru.silverhammer.conversion.IStringConverter;
import ru.silverhammer.injection.IInjector;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IMethodReflection;
import ru.silverhammer.swing.control.Control;

public class ButtonBarDecorator implements IDecorator<IControl<?, ?>, ButtonBar> {

	private final IStringConverter converter;
	private final IInjector injector;

	private IControl<?, ?> control;
	private JPanel stub;
	private ButtonBar annotation;
	private Object data;
	private final Map<Button, JButton> buttons = new HashMap<>();
	
	public ButtonBarDecorator(IStringConverter converter, IInjector injector) {
		this.converter = converter;
		this.injector = injector;
	}

	@Override
	public void init(ButtonBar annotation, Object data) {
		this.annotation = annotation;
		this.data = data;
		buttons.clear();

		stub = new JPanel(new BorderLayout());
		JPanel panel = new JPanel();
		if (annotation.location() == Location.Bottom || annotation.location() == Location.Top) {
			panel.setLayout(new GridBagLayout());
		} else {
			panel.setLayout(new GridBagLayout());
		}
		int i = 0;
		for (Button b : annotation.value()) {
			String caption = converter.getString(b.caption());
			JButton button = new JButton();
			if (b.icon().length() > 0) {
				URL url = getClass().getResource(b.icon());
				ImageIcon icon = new ImageIcon(url);
				Dimension d = new Dimension(icon.getIconWidth(), icon.getIconHeight());
				button.setPreferredSize(d);
				button.setIcon(icon);
				button.setToolTipText(caption);
			} else {
				button.setText(caption);
			}
			button.addActionListener(e -> {
				IMethodReflection method = new ClassReflection<>(data.getClass()).findMethod(b.pressedMethod());
				injector.invoke(data, method);
			});
			if (annotation.location() == Location.Bottom || annotation.location() == Location.Top) {
				panel.add(button, createHorizontalConstraints(i++));
			} else {
				panel.add(button, createVerticalConstraints(i++));
			}
			buttons.put(b, button);
		}

		if (annotation.location() == Location.Bottom || annotation.location() == Location.Top) {
			if (annotation.horizontalAlignment() == HorizontalAlignment.Left) {
				stub.add(panel, BorderLayout.WEST);
			} else if (annotation.horizontalAlignment() == HorizontalAlignment.Center) {
				stub.add(panel, BorderLayout.CENTER);
			} else if (annotation.horizontalAlignment() == HorizontalAlignment.Right) {
				stub.add(panel, BorderLayout.EAST);
			}
		} else {
			if (annotation.verticalAlignment() == VerticalAlignment.Top) {
				stub.add(panel, BorderLayout.NORTH);
			} else if (annotation.verticalAlignment() == VerticalAlignment.Center) {
				stub.add(panel, BorderLayout.CENTER);
			} else if (annotation.verticalAlignment() == VerticalAlignment.Bottom) {
				stub.add(panel, BorderLayout.SOUTH);
			}
		}

		if (annotation.location() == Location.Bottom) {
			panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		} else if (annotation.location() == Location.Top) {
			panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		} else if (annotation.location() == Location.Left) {
			panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		} else if (annotation.location() == Location.Right) {
			panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		}
	}

	@Override
	public void setControl(IControl<?, ?> control) {
		this.control = control;
		for (Button b : annotation.value()) {
			if (b.enabledMethod().length() > 0) {
				control.addValueListener(c -> {
					IMethodReflection method = new ClassReflection<>(data.getClass()).findMethod(b.enabledMethod());
					Object result = injector.invoke(data, method);
					boolean enabled = result instanceof Boolean ? (Boolean) result : true;
					buttons.get(b).setEnabled(enabled);
				});
			}
		}
		if (annotation.location() == Location.Bottom) {
			((Control<?, ?, ?>) control).add(stub, BorderLayout.SOUTH);
		} else if (annotation.location() == Location.Top) {
			((Control<?, ?, ?>) control).add(stub, BorderLayout.NORTH);
		} else if (annotation.location() == Location.Left) {
			((Control<?, ?, ?>) control).add(stub, BorderLayout.WEST);
		} else if (annotation.location() == Location.Right) {
			((Control<?, ?, ?>) control).add(stub, BorderLayout.EAST);
		}
	}

	@Override
	public IControl<?, ?> getControl() {
		return control;
	}

	private GridBagConstraints createVerticalConstraints(int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = y;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(y == 0 ? 0 : 5, 0, 0, 0);
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		return gbc;
	}

	private GridBagConstraints createHorizontalConstraints(int x) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(0, x == 0 ? 0 : 5, 0, 0);
		gbc.gridheight = 1;
		gbc.weighty = 1;
		return gbc;
	}

}
