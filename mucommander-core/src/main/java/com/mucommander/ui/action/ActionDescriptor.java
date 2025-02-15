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

package com.mucommander.ui.action;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * Each MuAction is registered with an object of ActionDescriptor type
 * that provides its properties. ActionDescriptor is an interface that 
 * defines those action's properties. 
 * 
 * @author Arik Hadas
 */
public interface ActionDescriptor {
	
	String getId();
	
	String getDescription();
	
	ActionCategory getCategory();
	
	String getLabel();
	
	String getLabelKey();
	
	KeyStroke getDefaultKeyStroke();
	
	KeyStroke getDefaultAltKeyStroke();
	
	ImageIcon getIcon();
	
	String getTooltip();

    /**
     * Returns <code>true</code> if the action requires parameters at creation time.
     *
     * @return <code>true</code> if the action requires parameters at creation time.
     */
    boolean isParameterized();
}
