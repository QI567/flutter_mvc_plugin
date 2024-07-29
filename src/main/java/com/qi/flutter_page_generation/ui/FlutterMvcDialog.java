package com.qi.flutter_page_generation.ui;

import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class FlutterMvcDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField mvcNameField;
    private JRadioButton yesRadioButton;
    private JRadioButton noRadioButton;
    private String mvcName;
    private boolean useGoRouter;
    private boolean isOk;

    public FlutterMvcDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        isOk = true;
        mvcName = mvcNameField.getText();
        useGoRouter = yesRadioButton.isSelected();
        if (StringUtils.isEmpty(mvcName)) {
            Messages.showErrorDialog("请输入MVC名称", "错误");
            return;
        }
        dispose();
    }
    private void onCancel() {
        isOk = false;
        dispose();
    }
    public String getMvcName() {
        return mvcName;
    }
    public boolean getUseGoRouter() {
        return useGoRouter;
    }
    public boolean isOk() {
        return isOk;
    }
    public static void main(String[] args) {
        FlutterMvcDialog dialog = new FlutterMvcDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
