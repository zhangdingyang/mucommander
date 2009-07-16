/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2009 Maxence Bernard
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

import com.mucommander.conf.impl.MuConfiguration;
import com.mucommander.ui.action.MuAction;
import com.mucommander.ui.action.ActionFactory;
import com.mucommander.ui.main.MainFrame;
import com.mucommander.ui.main.table.FileTable;

import java.util.Hashtable;

/**
 * This action toggles the 'Show folders first' option, which controls whether folders are displayed first in the
 * FileTable or mixed with regular files.
 *
 * @author Maxence Bernard
 */
public class ToggleShowFoldersFirstAction extends MuAction {

    public ToggleShowFoldersFirstAction(MainFrame mainFrame, Hashtable properties) {
        super(mainFrame, properties);
    }

    public void performAction() {
        FileTable activeTable = mainFrame.getActiveTable();
        boolean showFoldersFirst = !activeTable.getSortInfo().getFoldersFirst();
        activeTable.setFoldersFirst(showFoldersFirst);
        MuConfiguration.setVariable(MuConfiguration.SHOW_FOLDERS_FIRST, showFoldersFirst);
    }
    
    public static class Factory implements ActionFactory {

		public MuAction createAction(MainFrame mainFrame, Hashtable properties) {
			return new ToggleShowFoldersFirstAction(mainFrame, properties);
		}
    }
}
