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

package com.mucommander.ui.action.impl;

import java.util.Map;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.FileOperation;
import com.mucommander.desktop.ActionType;
import com.mucommander.ui.action.AbstractActionDescriptor;
import com.mucommander.ui.action.ActionCategory;
import com.mucommander.ui.action.ActionDescriptor;
import com.mucommander.ui.dialog.file.MkdirDialog;
import com.mucommander.ui.main.MainFrame;

/**
 * This action brings up the 'Make file' dialog which allows to create a new file in the currently active folder.
 *
 * @author Maxence Bernard
 */
public class MkfileAction extends ParentFolderAction {

    public MkfileAction(MainFrame mainFrame, Map<String,Object> properties) {
        super(mainFrame, properties);
    }

    protected void toggleEnabledState() {
        AbstractFile firstFile = mainFrame.getActiveTable().getFileTableModel().getFileAt(0);

        // If there is no file at all, do not rely on the action being supported by the current folder as this
        // would be incorrect for some filesystems which do not support operations consistently across the
        // filesystem (e.g. S3). In that case, err on the safe side and enable the action, even if the operation
        // end up not being supported.
        setEnabled(firstFile==null || firstFile.isFileOperationSupported(FileOperation.WRITE_FILE));
    }

    @Override
    public void performAction() {
        new MkdirDialog(mainFrame, true).showDialog();
    }

	@Override
	public ActionDescriptor getDescriptor() {
		return new Descriptor();
	}

    public static class Descriptor extends AbstractActionDescriptor {
		public String getId() { return ActionType.Mkfile.toString(); }

		public ActionCategory getCategory() { return ActionCategory.FILES; }
    }
}
