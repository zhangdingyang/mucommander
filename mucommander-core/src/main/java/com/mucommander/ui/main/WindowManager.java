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

package com.mucommander.ui.main;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.violetlib.aqua.AquaLookAndFeel;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.mucommander.Application;
import com.mucommander.commons.conf.ConfigurationEvent;
import com.mucommander.commons.conf.ConfigurationListener;
import com.mucommander.commons.runtime.OsFamily;
import com.mucommander.conf.MuConfigurations;
import com.mucommander.conf.MuPreference;
import com.mucommander.conf.MuPreferences;
import com.mucommander.extension.ExtensionManager;
import com.mucommander.ui.main.commandbar.CommandBar;
import com.mucommander.ui.main.frame.MainFrameBuilder;

/**
 * Window Manager is responsible for creating, disposing, switching,
 * in other words managing :) muCommander windows.
 *
 * @author Maxence Bernard, Arik Hadas
 */
//public class WindowManager implements ActionListener, WindowListener, ActivePanelListener, LocationListener, ConfigurationListener {
public class WindowManager implements WindowListener, ConfigurationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(WindowManager.class);
	
    // - MainFrame positioning --------------------------------------------------
    // --------------------------------------------------------------------------
    // The following constants are used to compute the proper position of a new MainFrame.

    /** MainFrame (main muCommander window) instances */
    private List<MainFrame> mainFrames;
    
    /** MainFrame currently being used (that has focus),
     * or last frame to have been used if muCommander doesn't have focus */	
    private MainFrame currentMainFrame;

    private static final WindowManager instance = new WindowManager();


    // - Initialization ---------------------------------------------------------
    // --------------------------------------------------------------------------
    /**
     * Installs all custom look and feels.
     */
    private static void installCustomLookAndFeels() {
        // All available custom look and feels.
        List<String> plafs = MuConfigurations.getPreferences().getListVariable(MuPreference.CUSTOM_LOOK_AND_FEELS, MuPreferences.CUSTOM_LOOK_AND_FEELS_SEPARATOR);

        // Tries to retrieve the custom look and feels list.
        if(plafs == null)
            return;

        // Goes through the list and install every custom look and feel we could find.
        // Look and feels that aren't supported under the current platform are ignored.
        for(String plaf : plafs) {
            try {installLookAndFeel(plaf);}
            catch(Throwable e) {
                LOGGER.info("Failed to install Look&Feel "+plaf, e);
            }
        }
    }

    /**
     * Creates a new instance of WindowManager.
     */
    private WindowManager() {
        mainFrames = new Vector<MainFrame>();

        FlatDarculaLaf.installLafInfo();
        FlatDarkLaf.installLafInfo();
        FlatLightLaf.installLafInfo();
        FlatIntelliJLaf.installLafInfo();

        if (OsFamily.MAC_OS.isCurrent()) {
            AquaLookAndFeel aquaLookAndFeel = new AquaLookAndFeel();
            UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo(aquaLookAndFeel.getName(), aquaLookAndFeel.getClass().getName()));
        }

        // Notifies Swing that look&feels must be loaded as extensions.
        // This is necessary to ensure that look and feels placed in the extensions folder
        // are accessible.
        UIManager.getDefaults().put("ClassLoader", ExtensionManager.getClassLoader());

        // Installs all custom look and feels.
        installCustomLookAndFeels();
        
        // Sets custom lookAndFeel if different from current lookAndFeel
        String lnfName = MuConfigurations.getPreferences().getVariable(MuPreference.LOOK_AND_FEEL);
        if(lnfName!=null && !lnfName.equals(UIManager.getLookAndFeel().getName()))
            setLookAndFeel(lnfName);

        if(lnfName == null)
            LOGGER.debug("Could load look'n feel from preferences");
        
        MuConfigurations.addPreferencesListener(this);
    }

    public static void setDefaultLookAndFeel() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        if (OsFamily.LINUX.isCurrent()) {
            UIManager.setLookAndFeel(FlatLightLaf.class.getName());
        }
    }

    /**
     * Returns the sole instance of WindowManager.
     *
     * @return the sole instance of WindowManager
     */
    public static WindowManager getInstance() {
        return instance;
    }
    
	
    /**
     * Returns the <code>MainFrame</code> instance that was last active. Note that the returned <code>MainFrame</code>
     * may or may not be currently active.
     *
     * @return the <code>MainFrame</code> instance that was last active
     */
    public static MainFrame getCurrentMainFrame() {
        return instance.currentMainFrame;
    }
	
    /**
     * Returns a <code>Vector</code> of all <code>MainFrame</code> instances currently displaying.
     *
     * @return a <code>Vector</code> of all <code>MainFrame</code> instances currently displaying
     */
    public static List<MainFrame> getMainFrames() {
        return instance.mainFrames;
    }

    /**
     * Refreshes all panels in all frames in an asynchronous manner.
     */
    public static void tryRefreshCurrentFolders() {
        // Starts with the main frame to make sure that results are immediately
        // visible to the user.
    	instance.currentMainFrame.tryRefreshCurrentFolders();
        for(MainFrame mainFrame : instance.mainFrames)
            if(mainFrame != instance.currentMainFrame)
                mainFrame.tryRefreshCurrentFolders();
    }

    /**
     * Creates a new MainFrame and makes it visible on the screen, on top of any other frames.
     *
     * @param leftFolders initial paths for the left frame.
     * @param rightFolders initial paths for the right frame.
     * @return the newly created MainFrame.
     */
    public static synchronized void createNewMainFrame(MainFrameBuilder mainFrameBuilder) {
        Collection<MainFrame> newMainFrames = mainFrameBuilder.build();

        // To catch user window closing actions
        newMainFrames.forEach(frame -> frame.addWindowListener(instance));

        // Adds the new MainFrame to the vector
        instance.mainFrames.addAll(newMainFrames);

        // Set new window's title. Window titles show window number only if there is more than one window.
        // So if a second window was just created, we update first window's title so that it shows window number (#1).
        instance.mainFrames.forEach(MainFrame::updateWindowTitle);

        // Make frames visible
        newMainFrames.forEach(frame -> frame.setVisible(true));

        if (!instance.mainFrames.isEmpty()) {
            // get the main frame that was previously selected
            int mainFrameToSelect = mainFrameBuilder.getSelectedFrame();
            // if we cannot restore previous selection, default to the first main frame
            if (mainFrameToSelect >= instance.mainFrames.size())
                mainFrameToSelect = 0;
            instance.mainFrames.get(mainFrameToSelect).toFront();
        }
    }

    /**
     * Returns the index of the currently selected window
     * @return index of currently selected window
     */
    public static int getCurrentWindowIndex() {
    	return instance.mainFrames.indexOf(instance.currentMainFrame);
    }
	
    /**
     * Switches to the next MainFrame, in the order of which they were created.
     */
    public static void switchToNextWindow() {
        int frameIndex = getCurrentWindowIndex();
        MainFrame mainFrame = instance.mainFrames.get((frameIndex+1) % instance.mainFrames.size());
        mainFrame.toFront();
    }

    /**
     * Switches to previous MainFrame, in the order of which they were created.
     */
    public static void switchToPreviousWindow() {
        int frameIndex = getCurrentWindowIndex();
        int nbFrames = instance.mainFrames.size();
        MainFrame mainFrame = instance.mainFrames.get((frameIndex-1+nbFrames) % nbFrames);
        mainFrame.toFront();
    }

    public static void installLookAndFeel(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        LookAndFeel plaf;

        plaf = (LookAndFeel)Class.forName(className, true, ExtensionManager.getClassLoader()).newInstance();
        if(plaf.isSupportedLookAndFeel())
            UIManager.installLookAndFeel(plaf.getName(), plaf.getClass().getName());
    }

    /**
     * Changes LooknFeel to the given one, updating the UI of each MainFrame.
     *
     * @param lnfName name of the new LooknFeel to use
     */
    private void setLookAndFeel(String lnfName) {
        try {
            // Initializes class loading.
            // This is necessary due to Swing's UIDefaults.LazyProxyValue behaviour that just
            // won't use the right ClassLoader instance to load resources.
            Thread currentThread = Thread.currentThread();
            ClassLoader oldLoader = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(ExtensionManager.getClassLoader());

            UIManager.setLookAndFeel(lnfName);

            // Restores the contextual ClassLoader.
            currentThread.setContextClassLoader(oldLoader);

            mainFrames.forEach(MainFrame::updateFileTablesHeaderRenderer);
            mainFrames.forEach(SwingUtilities::updateComponentTreeUI);
        }
        catch(Throwable e) {
            LOGGER.debug("Exception caught", e);
        }
    }


    ////////////////////////////
    // WindowListener methods //
    ////////////////////////////

    public void windowActivated(WindowEvent e) {
        Object source = e.getSource();
        
        // Return if event doesn't originate from a MainFrame (e.g. ViewerFrame or EditorFrame)
        if(!(source instanceof MainFrame))
            return;

        currentMainFrame = (MainFrame)e.getSource();
        // Let MainFrame know that it is active in the foreground
        currentMainFrame.setForegroundActive(true);

        // Resets shift mode to false, since keyReleased events may have been lost during window switching
        CommandBar commandBar = currentMainFrame.getCommandBar();
        if(commandBar!=null)
            commandBar.setAlternateActionsMode(false);
    }

    public void windowDeactivated(WindowEvent e) {
        Object source = e.getSource();

        // Workaround for JRE bug #4841881 (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4841881) /
        // which causes Alt+Tab to focus the menu bar under certain L&F.
        // This bug has also been reported as muCommander bug #89.
        MenuSelectionManager.defaultManager().clearSelectedPath();

        // Return if event doesn't originate from a MainFrame (e.g. ViewerFrame or EditorFrame)
        if(!(source instanceof MainFrame))
            return;

        // Let MainFrame know that it is not active anymore
        ((MainFrame)e.getSource()).setForegroundActive(false);
    }

    public void windowClosing(WindowEvent e) {
    }
    
    /**
     * windowClosed is synchronized so that it doesn't get called while quit() is executing.
     */
    public synchronized void windowClosed(WindowEvent e) {
        LOGGER.trace("called");

        Object source = e.getSource();

        if(source instanceof MainFrame) {
            // Remove disposed MainFrame from the MainFrame list
            int frameIndex = mainFrames.indexOf(source);

            mainFrames.remove(source);

            // Update following windows titles to reflect the MainFrame's disposal.
            // Window titles show window number only if there is more than one window.
            // So if there is only one window left, we update first window's title so that it removes window number (#1).
            int nbFrames = mainFrames.size();
            if(nbFrames==1) {
                mainFrames.get(0).updateWindowTitle();
            }
            else {
                if(frameIndex!=-1) {
                    for(int i=frameIndex; i<nbFrames; i++)
                        mainFrames.get(i).updateWindowTitle();
                }
            }
        }

        // Test if there is at least one MainFrame still showing
        if(mainFrames.size()>0)
            return;

        // Test if there is at least one window (viewer, editor...) still showing
        Frame frames[] = Frame.getFrames();
        int nbFrames = frames.length;
        Frame frame;
        for(int i=0; i<nbFrames; i++) {
            frame = frames[i];
            if(frame.isShowing()) {
                LOGGER.debug("found active frame#"+i);
                return;
            }
        }

        // No more window showing, initiate shutdown sequence
        Application.initiateShutdown();
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }


    ///////////////////////////////////
    // ConfigurationListener methods //
    ///////////////////////////////////

    /**
     * Listens to certain configuration variables.
     */
    public void configurationChanged(ConfigurationEvent event) {
    	String var = event.getVariable();

    	// /!\ font.size is set after font.family in AppearancePrefPanel
    	// that's why we only listen to this one in order not to change Font twice
    	if (var.equals(MuPreferences.LOOK_AND_FEEL)) {
    		String lnfName = event.getValue();

    		if(!UIManager.getLookAndFeel().getClass().getName().equals(lnfName))
    			setLookAndFeel(lnfName);
    	}
    }
}
