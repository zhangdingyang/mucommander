/*
 * This file is part of muCommander, http://www.mucommander.com
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.job.impl;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.archive.AbstractRWArchiveFile;
import com.mucommander.commons.file.util.FileSet;
import com.mucommander.job.FileCollisionChecker;
import com.mucommander.job.FileJobAction;
import com.mucommander.text.Translator;
import com.mucommander.ui.dialog.DialogAction;
import com.mucommander.ui.dialog.QuestionDialog;
import com.mucommander.ui.dialog.file.FileCollisionDialog;
import com.mucommander.ui.dialog.file.FileCollisionRenameDialog;
import com.mucommander.ui.dialog.file.ProgressDialog;
import com.mucommander.ui.main.MainFrame;

import java.io.IOException;

/**
 * This class is the parent class of {@link com.mucommander.job.impl.CopyJob} and {@link com.mucommander.job.impl.MoveJob} and
 * allows them to share methods and fields.
 *
 * @author Maxence Bernard, Mariusz Jakubowski
 * @see com.mucommander.job.impl.CopyJob
 * @see com.mucommander.job.impl.MoveJob
 */
public abstract class AbstractCopyJob extends TransferFileJob {
    
    /** Base destination folder */
    protected AbstractFile baseDestFolder;
    
    /** New filename in destination */
    protected String newName;

    /** Default choice when encountering an existing file */
    protected FileCollisionDialog.FileCollisionAction defaultFileExistsAction = FileCollisionDialog.FileCollisionAction.ASK;
    
    /** Title used for error dialogs */
    protected String errorDialogTitle;
    
    protected boolean append;
    
    /** The archive that contains the destination files (may be null) */
    protected AbstractRWArchiveFile archiveToOptimize;

    /** True when an archive is being optimized */
    protected boolean isOptimizingArchive;

    /**
     * Creates a new <code>AbstractCopyJob</code>.
     *
     * @param progressDialog dialog which shows this job's progress
     * @param mainFrame mainFrame this job has been triggered by
     * @param files files which are going to be copied
     * @param destFolder destination folder where the files will be copied
     * @param newName the new filename in the destination folder, can be <code>null</code> in which case the original filename will be used.
     * @param fileExistsAction default action to be performed when a file already exists in the destination.
     */
    public AbstractCopyJob(ProgressDialog progressDialog, MainFrame mainFrame,
            FileSet files, AbstractFile destFolder, String newName, FileCollisionDialog.FileCollisionAction fileExistsAction) {
        super(progressDialog, mainFrame, files);

        this.baseDestFolder = destFolder;
        this.newName = newName;        
        this.defaultFileExistsAction = fileExistsAction;
    }

    /**
     * Creates a destination file given a destination folder and a new file name.
     * @param destFolder a destination folder
     * @param destFileName a destination file name
     * @return the destination file or null if it cannot be created
     */
    protected AbstractFile createDestinationFile(AbstractFile file, AbstractFile destFolder, String destFileName) {
        do {    // Loop for retry
            try {
                return destFolder.getDirectChild(destFileName, file);
            } catch(IOException e) {
                // Destination file couldn't be instantiated

                DialogAction ret = showErrorDialog(errorDialogTitle, Translator.get("cannot_write_file", destFileName));
                // Retry loops
                if (ret == FileJobAction.RETRY) {
                    continue;
                }
                // Cancel or close dialog return false
                return null;
                // Skip continues
            }
        } while(true);
    }
    
    /**
     * Checks if there is a file collision (file exists in the destination).
     * If there is no collision this method returns destFile.
     * If there is a collision this method returns: <ul>
     *  <li>null if a user cancelled the transfer 
     *  <li>null if a user skipped the file
     *  <li>destFile if a user resumed the transfer (and sets append flag)
     *  <li>destFile if a user has chosen to overwrite the file
     *  <li>new file if a user renamed the file
     *  </ul>
     * @param file a source file
     * @param destFolder a destination folder
     * @param destFile a destination file
     * @param allowCaseVariation if true,
     * @return destFile the new destination file
     */
    protected AbstractFile checkForCollision(AbstractFile file, AbstractFile destFolder, AbstractFile destFile, boolean allowCaseVariation) {
        append = false;
        while (true) {
            // Check for file collisions (file exists in the destination, destination subfolder of source, ...)
            // if a default action hasn't been specified
            int collision = FileCollisionChecker.checkForCollision(file, destFile);
            
            // If allowCaseVariation is true and both files are equal, test if the destination filename is a variation
            // of the original filename with a different case. If that is the case, do not warn about the source and
            // destination being the same.
            if (allowCaseVariation && collision == FileCollisionChecker.SAME_SOURCE_AND_DESTINATION) {
                String sourceFileName = file.getName();
                String destFileName = destFile.getName();
                if (sourceFileName.equalsIgnoreCase(destFileName) && !sourceFileName.equals(destFileName)) {
                    break;
                }
            }
            
            // Handle collision, asking the user what to do or using a default action to resolve the collision 
            if (collision != FileCollisionChecker.NO_COLLISION) {
                FileCollisionDialog.FileCollisionAction choice;
                // Use default action if one has been set, if not show up a dialog
                if (defaultFileExistsAction == FileCollisionDialog.FileCollisionAction.ASK) {
                    FileCollisionDialog dialog = new FileCollisionDialog(getProgressDialog(), getMainFrame(), collision, file, destFile, true, true);
                    choice = (FileCollisionDialog.FileCollisionAction) waitForUserResponse(dialog);
                    // If 'apply to all' was selected, this choice will be used for any other files (user will not be asked again)
                    if (dialog.applyToAllSelected()) {
                        defaultFileExistsAction = choice;
                    }
                } else {
                    choice = defaultFileExistsAction;
                }
                // Cancel, skip or close dialog
                if (choice == QuestionDialog.DIALOG_DISPOSED_ACTION || choice == FileCollisionDialog.FileCollisionAction.CANCEL) {
                    interrupt();
                    return null;
                }
                // Skip file
                else if (choice == FileCollisionDialog.FileCollisionAction.SKIP) {
                    return null;
                }
                // Append to file (resume file copy)
                else if (choice == FileCollisionDialog.FileCollisionAction.RESUME) {
                    append = true;
                    break;
                }
                // Overwrite file
                else if (choice == FileCollisionDialog.FileCollisionAction.OVERWRITE) {
                    // Do nothing, simply continue
                    break;
                }
                //  Overwrite file if destination is older
                else if (choice == FileCollisionDialog.FileCollisionAction.OVERWRITE_IF_OLDER) {
                    // Overwrite if file is newer (strictly)
                    if (file.getDate() <= destFile.getDate()) {
                        return null;
                    }
                    break;
                } else if (choice == FileCollisionDialog.FileCollisionAction.OVERWRITE_IF_SIZE_DIFFERS) {
                    if (file.getSize() == destFile.getSize()) {
                        return null;
                    }
                    break;
                } else if (choice == FileCollisionDialog.FileCollisionAction.RENAME) {
                    setPaused(true);
                    FileCollisionRenameDialog dlg = new FileCollisionRenameDialog(getMainFrame(), destFile);
                    String destFileName = (String) waitForUserResponseObject(dlg);
                    setPaused(false);
                    if (destFileName != null) {
                        destFile = createDestinationFile(file, destFolder, destFileName);
                    } else {
                        // turn on FileCollisionDialog, so we don't loop indefinitely
                        defaultFileExistsAction = FileCollisionDialog.FileCollisionAction.ASK;
                    }
                    // continue with collision checking
                    continue;
                }
            }
            break;    // no collision
        }
        return destFile;
    }
    
    /**
     * Optimizes the given writable archive file and notifies the user in case of an error.
     *
     * @param rwArchiveFile the writable archive file to optimize
     */
    protected void optimizeArchive(AbstractRWArchiveFile rwArchiveFile) {
        isOptimizingArchive = true;

        while(true) {
            try {
                archiveToOptimize = rwArchiveFile;
                archiveToOptimize.optimizeArchive();

                break;
            } catch(IOException e) {
                if (showErrorDialog(errorDialogTitle, Translator.get("error_while_optimizing_archive", rwArchiveFile.getName())) == FileJobAction.RETRY) {
                    continue;
                }

                break;
            }
        }

        isOptimizingArchive = false;
    }

}
