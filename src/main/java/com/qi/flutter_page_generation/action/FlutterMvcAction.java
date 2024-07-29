package com.qi.flutter_page_generation.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.qi.flutter_page_generation.ui.FlutterMvcDialog;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FlutterMvcAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        FlutterMvcDialog flutterMvcDialog = new FlutterMvcDialog();
        flutterMvcDialog.pack();
        flutterMvcDialog.setLocationRelativeTo(null);
        flutterMvcDialog.setVisible(true);
        if (!flutterMvcDialog.isOk()) {
            return;
        }
        String name = flutterMvcDialog.getMvcName();
        boolean useGoRouter = flutterMvcDialog.getUseGoRouter();
        // 将驼峰转下划线
        if (name.matches(".*[A-Z].*")) {
            name = name.replaceAll("([A-Z])", "_$1").toLowerCase();
            if (name.startsWith("_")) {
                name = name.substring(1);
            }
        }
        String pageFileName = name + "_page.dart";
        String controllerFileName = name + "_controller.dart";
        // 将下划线转驼峰
        boolean underline = false;
        StringBuilder sb = new StringBuilder();
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        for (char c : chars) {
            if (c == '_') {
                underline = true;
            } else {
                if (underline) {
                    sb.append(Character.toUpperCase(c));
                    underline = false;
                } else {
                    sb.append(c);
                }
            }
        }
        String pageClassName = sb + "Page";
        String controllerClassName = sb + "Controller";
        IdeView ideView = e.getData(LangDataKeys.IDE_VIEW);
        if (ideView == null) {
            Messages.showMessageDialog(project, "生成MVC代码失败", "错误", Messages.getErrorIcon());
            return;
        }
        PsiDirectory chooseDirectory = ideView.getOrChooseDirectory();
        if (chooseDirectory == null) {
            Messages.showMessageDialog(project, "获取项目路径失败", "错误", Messages.getErrorIcon());
            return;
        }
        File dir = new File(chooseDirectory.getVirtualFile().getPath(), name);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Messages.showErrorDialog("创建MVC路径失败", "错误");
                return;
            }
        }
        generatePage(pageClassName, controllerClassName, controllerFileName, useGoRouter, new File(dir, pageFileName));
        generateController(controllerClassName, new File(dir, controllerFileName));
    }

    private void generateController(String controllerClassName, File file) {
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("controllerClassName", controllerClassName);
                generateDartFile(map, "templates/controller.ftl", file);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    void generatePage(String className, String controllerClassName, String controllerFileFile, boolean userGoRouter, File destFile) {
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("pageClassName", className);
                map.put("controllerClassName", controllerClassName);
                map.put("controllerFile", controllerFileFile);
                map.put("useGoRouter", userGoRouter);
                generateDartFile(map, "templates/page.ftl", destFile);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    void generateDartFile(Map<String, Object> data, String templateName, File destFile) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "");
        OutputStreamWriter writer;
        try {
            Template template = configuration.getTemplate(templateName);
            writer = new OutputStreamWriter(new FileOutputStream(destFile));
            template.process(data, writer);
            writer.flush();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
