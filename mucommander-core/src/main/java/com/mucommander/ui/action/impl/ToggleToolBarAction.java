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

import javax.swing.JPanel;

import com.mucommander.conf.MuConfigurations;
import com.mucommander.conf.MuPreference;
import com.mucommander.conf.MuPreferences;
import com.mucommander.desktop.ActionType;
import com.mucommander.text.Translator;
import com.mucommander.ui.action.AbstractActionDescriptor;
import com.mucommander.ui.action.ActionCategory;
import com.mucommander.ui.action.ActionDescriptor;
import com.mucommander.ui.action.MuAction;
import com.mucommander.ui.main.MainFrame;

/**
 * This action shows/hides the current MainFrame's {@link com.mucommander.ui.main.toolbar.ToolBar} depending on its
 * current visible state: if it is visible, hides it, if not shows it.
 *
 * <p>This action's label will be updated to reflect the current visible state.
 *
 * <p>Each time this action is executed, the new current visible state is stored in the configuration so that
 * new MainFrame windows will use it to determine whether the ToolBar has to be made visible or not.
 *
 * @author Maxence Bernard
 */
public class ToggleToolBarAction extends MuAction {

    public ToggleToolBarAction(MainFrame mainFrame, Map<String,Object> properties) {
        super(mainFrame, properties);
        updateLabel(MuConfigurations.getPreferences().getVariable(MuPreference.TOOLBAR_VISIBLE, MuPreferences.DEFAULT_TOOLBAR_VISIBLE));
    }

    private void updateLabel(boolean visible) {
        setLabel(Translator.get(visible?ActionType.ToggleToolBar+".hide":ActionType.ToggleToolBar+".show"));
    }

    @Override
    public void performAction() {
        JPanel toolBarPanel = mainFrame.getToolBarPanel();
        boolean visible = !toolBarPanel.isVisible();
        // Save the last toolbar visible state in the configuration, this will become the default for new MainFrame windows.
        MuConfigurations.getPreferences().setVariable(MuPreference.TOOLBAR_VISIBLE, visible);
        // Change the label to reflect the new toolbar state
        updateLabel(visible);
        // Show/hide the toolbar
        toolBarPanel.setVisible(visible);
        mainFrame.validate();
    }

	@Override
	public ActionDescriptor getDescriptor() {
		return new Descriptor();
	}

    public static class Descriptor extends AbstractActionDescriptor {
		public String getId() { return ActionType.ToggleToolBar.toString(); }

		public ActionCategory getCategory() { return ActionCategory.VIEW; }

        @Override
        public String getLabelKey() { return ActionType.ToggleToolBar+".show"; }
    }
}
