package com.unitedhuskies.seamcarving;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class FocusTextField extends JTextField{
	FocusTextField(String str){
		super(str);
		addFocusListener((FocusListener) new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				FocusTextField.this.select(0, getText().length());
			}

			@Override
			public void focusLost(FocusEvent e) {
				FocusTextField.this.select(0, 0);
			}
			
		});
	}
	

}
