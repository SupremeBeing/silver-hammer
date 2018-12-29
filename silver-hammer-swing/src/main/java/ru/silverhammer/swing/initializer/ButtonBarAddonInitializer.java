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
package ru.silverhammer.swing.initializer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import ru.silverhammer.core.Location;
import ru.silverhammer.core.initializer.IInitializer;
import ru.silverhammer.core.string.IStringProcessor;
import ru.silverhammer.injection.Inject;
import ru.silverhammer.injection.Injector;
import ru.silverhammer.reflection.ClassReflection;
import ru.silverhammer.reflection.IFieldReflection;
import ru.silverhammer.reflection.IMethodReflection;
import ru.silverhammer.swing.control.Control;
import ru.silverhammer.swing.initializer.annotation.ButtonBarAddon;
import ru.silverhammer.swing.initializer.annotation.ButtonBarAddon.Button;

public class ButtonBarAddonInitializer implements IInitializer<Control<?, ?>, ButtonBarAddon> {

	private final IStringProcessor processor;
	private final Injector injector; 
	
	public ButtonBarAddonInitializer(@Inject IStringProcessor processor, @Inject Injector injector) {
		this.processor = processor;
		this.injector = injector;
	}

	@Override
	public void init(Control<?, ?> control, ButtonBarAddon annotation, Object data, IFieldReflection field) {
		JPanel stub = new JPanel(new BorderLayout());
		JPanel panel = new JPanel();
		if (annotation.location() == Location.Bottom || annotation.location() == Location.Top) {
			panel.setLayout(new GridLayout(1, 0, 5, 0));
		} else {
			panel.setLayout(new GridLayout(0, 1, 0, 5));
		}
		for (Button b : annotation.value()) {
			String caption = processor.getString(b.caption());
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
				IMethodReflection method = new ClassReflection<>(data.getClass()).findMethod(b.methodName());
				injector.invoke(data, method);
			});
			panel.add(button);
		}
		if (annotation.location() == Location.Bottom) {
			stub.add(panel, BorderLayout.EAST);
			panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			control.add(stub, BorderLayout.SOUTH);
		} else if (annotation.location() == Location.Top) {
			stub.add(panel, BorderLayout.EAST);
			panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			control.add(stub, BorderLayout.NORTH);
		} else if (annotation.location() == Location.Left) {
			stub.add(panel, BorderLayout.NORTH);
			panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			control.add(stub, BorderLayout.WEST);
		} else if (annotation.location() == Location.Right) {
			stub.add(panel, BorderLayout.NORTH);
			panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			control.add(stub, BorderLayout.EAST);
		}
	}
}
