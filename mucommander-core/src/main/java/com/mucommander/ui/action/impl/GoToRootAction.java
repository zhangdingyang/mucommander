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
import com.mucommander.desktop.ActionType;
import com.mucommander.ui.action.AbstractActionDescriptor;
import com.mucommander.ui.action.ActionCategory;
import com.mucommander.ui.action.ActionDescriptor;
import com.mucommander.ui.main.FolderPanel;
import com.mucommander.ui.main.MainFrame;

/**
 * This action changes the current folder of the currently active FolderPanel to the current folder's root.
 * This action only gets enabled when the current folder has a parent.
 *
 * @author Maxence Bernard
 */
public class GoToRootAction extends ActiveTabAction {

    public GoToRootAction(MainFrame mainFrame, Map<String,Object> properties) {
        super(mainFrame, properties);
    }

    /**
     * Enables or disables this action based on the currently active folder's
     * has a parent and current tab is not locked, this action will be enabled,
     * if not it will be disabled.
     */
    @Override
    protected void toggleEnabledState() {
        setEnabled(!mainFrame.getActivePanel().getTabs().getCurrentTab().isLocked() &&
        		    mainFrame.getActivePanel().getCurrentFolder().getParent()!=null);
    }
    
    @Override
    public void performAction() {
        // Changes the current folder to make it the current folder's root folder.
        // Does nothing if the current folder already is the root.
        FolderPanel folderPanel = mainFrame.getActivePanel();
        AbstractFile currentFolder = folderPanel.getCurrentFolder();
        folderPanel.tryChangeCurrentFolder(currentFolder.getRoot());
    }

	@Override
	public ActionDescriptor getDescriptor() {
		return new Descriptor();
	}

    public static class Descriptor extends AbstractActionDescriptor {
		public String getId() { return ActionType.GoToRoot.toString(); }

		public ActionCategory getCategory() { return ActionCategory.NAVIGATION; }
    }
}
