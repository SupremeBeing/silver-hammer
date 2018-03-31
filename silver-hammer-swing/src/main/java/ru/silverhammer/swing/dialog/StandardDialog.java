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
package ru.silverhammer.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class StandardDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = -1928366951191111057L;

	private JScrollPane scroll;
	private JButton acceptButton;
	private JButton cancelButton;

	private boolean accepted;
	private boolean canAccept = true;
	private boolean canCancel = true;
	
	public StandardDialog(Window owner) {
		super(owner);
	}

	public boolean isAccepted() {
		return accepted;
	}
	
	public void setAcceptButtonVisible(boolean visible) {
		acceptButton.setVisible(visible);
	}

	public void setCancelButtonVisible(boolean visible) {
		cancelButton.setVisible(visible);
	}

	public void setAcceptText(String text) {
		acceptButton.setText(text);
	}

	public void setCancelText(String text) {
		cancelButton.setText(text);
	}

	@Override
	protected void dialogInit() {
		super.dialogInit();
		
		JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        scroll = new JScrollPane();
        scroll.setBorder(null);
		mainPanel.add(scroll, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel stub = new JPanel(new BorderLayout());
		stub.add(buttonPanel, BorderLayout.EAST);
		
		mainPanel.add(stub, BorderLayout.SOUTH);
		
		acceptButton = new JButton("OK");
		buttonPanel.add(acceptButton);
        getRootPane().setDefaultButton(acceptButton);
        acceptButton.addActionListener((e) -> accept());

		cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
        cancelButton.addActionListener((e) -> cancel());
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
        getRootPane().getActionMap().put("CANCEL", new AbstractAction() {
			private static final long serialVersionUID = -127075625119822228L;

			@Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
		
		addWindowListener(this);
		
		if (getOwner() != null) {
			setLocationRelativeTo(getOwner());
		}
		setModalityType(ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	private void cancel() {
		if (canCancel) {
			cancelled();
			accepted = false;
			setVisible(false);
			dispose();
		}
	}

	private void accept() {
		if (canAccept) {
			accepted();
			accepted = true;
			setVisible(false);
			dispose();
		}
	}
	
	protected void accepted() {}
	
	protected void cancelled() {}
	
	public void setCanAccept(boolean canAccept) {
		this.canAccept = canAccept;
		acceptButton.setEnabled(canAccept);
	}
	
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
		cancelButton.setEnabled(canCancel);
	}
	
	public void setContent(Component component) {
        scroll.setViewportView(component);
		pack();
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
        cancel();
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
