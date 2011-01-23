/*******************************************************************************
 * Copyright (c) 2010 Nicolas Roduit.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 ******************************************************************************/
package org.weasis.dicom.explorer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.gui.util.JMVUtils;

public class LocalImport extends AbstractItemDialogPage implements ImportDicom {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalImport.class);

    private JCheckBox chckbxSearch;
    private JLabel lblImportAFolder;
    private JTextField textField;
    private JButton button;
    private File[] files;

    public LocalImport() {
        setTitle(Messages.getString("LocalImport.local_dev")); //$NON-NLS-1$
        initGUI();
        initialize(true);
    }

    public void initGUI() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);

        lblImportAFolder = new JLabel(Messages.getString("LocalImport.imp_files")); //$NON-NLS-1$
        GridBagConstraints gbc_lblImportAFolder = new GridBagConstraints();
        gbc_lblImportAFolder.anchor = GridBagConstraints.WEST;
        gbc_lblImportAFolder.insets = new Insets(5, 5, 0, 0);
        gbc_lblImportAFolder.gridx = 0;
        gbc_lblImportAFolder.gridy = 0;
        add(lblImportAFolder, gbc_lblImportAFolder);

        textField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.anchor = GridBagConstraints.WEST;
        gbc_textField.insets = new Insets(5, 2, 0, 0);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        // textField.setColumns(10);
        JMVUtils.setPreferredWidth(textField, 375, 375);
        add(textField, gbc_textField);

        button = new JButton(" ... "); //$NON-NLS-1$
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                browseImgFile();
            }
        });
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.anchor = GridBagConstraints.WEST;
        gbc_button.insets = new Insets(5, 5, 0, 5);
        gbc_button.gridx = 2;
        gbc_button.gridy = 0;
        add(button, gbc_button);

        chckbxSearch = new JCheckBox(Messages.getString("LocalImport.recursive")); //$NON-NLS-1$
        chckbxSearch.setSelected(true);
        GridBagConstraints gbc_chckbxSearch = new GridBagConstraints();
        gbc_chckbxSearch.gridwidth = 3;
        gbc_chckbxSearch.insets = new Insets(5, 5, 0, 0);
        gbc_chckbxSearch.anchor = GridBagConstraints.NORTHWEST;
        gbc_chckbxSearch.gridx = 0;
        gbc_chckbxSearch.gridy = 1;
        add(chckbxSearch, gbc_chckbxSearch);

        final JLabel label = new JLabel();
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.weighty = 1.0;
        gridBagConstraints_4.weightx = 1.0;
        gridBagConstraints_4.gridy = 4;
        gridBagConstraints_4.gridx = 2;
        add(label, gridBagConstraints_4);
    }

    protected void initialize(boolean afirst) {
        if (afirst) {

        }
    }

    public void browseImgFile() {
        String directory = ""; //$NON-NLS-1$
        if (files != null && files.length > 0) {
            directory = files[0].isDirectory() ? files[0].getPath() : files[0].getParent();
        } else {
            String path = textField.getText().trim();
            if (path != null && !path.equals("") && !path.equals(Messages.getString("LocalImport.multi_dir"))) { //$NON-NLS-1$ //$NON-NLS-2$
                File file = new File(path);
                if (file.canRead()) {
                    directory = file.isDirectory() ? file.getPath() : file.getParent();
                }
            }
        }

        JFileChooser fileChooser = new JFileChooser(directory);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);
        // FileFormatFilter.setImageDecodeFilters(fileChooser);
        File[] selectedFiles = null;
        if (fileChooser.showOpenDialog(this) != 0 || (selectedFiles = fileChooser.getSelectedFiles()) == null) {
            files = null;
            return;
        } else {
            files = selectedFiles;
            textField.setText(files.length == 1 ? files[0].getPath() : Messages.getString("LocalImport.multi_dir")); //$NON-NLS-1$
        }
    }

    public void resetSettingsToDefault() {
        initialize(false);
    }

    public void applyChange() {

    }

    protected void updateChanges() {
    }

    @Override
    public void closeAdditionalWindow() {
        applyChange();
    }

    @Override
    public void resetoDefaultValues() {
    }

    @Override
    public void importDICOM(DicomModel dicomModel, JProgressBar info) {
        if (files == null) {
            String path = textField.getText().trim();
            if (path != null && !path.equals("") && !path.equals(Messages.getString("LocalImport.multi_dir"))) { //$NON-NLS-1$ //$NON-NLS-2$
                File file = new File(path);
                if (file.canRead()) {
                    files = new File[] { file };
                } else {
                    try {
                        file = new File(new URI(path));
                        if (file.canRead()) {
                            files = new File[] { file };
                        }
                    } catch (Exception e) {
                        LOGGER.error("Cannot import DICOM from {}", path);
                    }
                }
            }
        }
        if (files != null) {
            LoadLocalDicom dicom = new LoadLocalDicom(files, chckbxSearch.isSelected(), dicomModel);
            dicom.setProgressBar(info);
            DicomModel.loadingExecutor.execute(dicom);
        }
    }
}
