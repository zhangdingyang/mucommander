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
package com.mucommander.ui.viewer;

import static com.mucommander.snapshot.MuSnapshot.FILE_PRESENTER_SECTION;

import com.mucommander.snapshot.MuSnapshot;

/**
 * Viewer preferences.
 */
public enum ViewerPreferences {

    SHOW_FULLSCREEN("show_fullscreen", "file_viewer.fullscreen", "false"),
    WINDOW_POSITION_X("window_position_x", "file_viewer.window_position_x", ""),
    WINDOW_POSITION_Y("window_position_y", "file_viewer.window_position_y", ""),
    WINDOW_WIDTH("window_width", "file_viewer.window_width", ""),
    WINDOW_HEIGHT("window_height", "file_viewer.window_height", "")
    ;

    private String prefKey;
    private String i18nKey;
    private String currentValue;

    ViewerPreferences(String prefKey, String i18nKey, String defaultValue) {
        this.prefKey =  FILE_PRESENTER_SECTION + "." + prefKey;
        this.i18nKey = i18nKey;
        this.currentValue = MuSnapshot.getSnapshot().getVariable(this.prefKey, defaultValue);
    }

    public String getPrefKey() {
        return prefKey;
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public String getValue() {
        return currentValue;
    }

    public void setValue(String value) {
        currentValue = value;
    }
}
