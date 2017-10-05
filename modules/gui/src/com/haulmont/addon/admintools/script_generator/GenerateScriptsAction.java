package com.haulmont.addon.admintools.script_generator;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;

import java.util.Set;


public class GenerateScriptsAction extends ItemTrackingAction {

    public static final String ACTION_ID = "generateScripts";

    protected WindowManager.OpenType openType;

    /**
     * The simplest constructor. The action has default name and opens the editor screen in THIS tab.
     * @param target    component containing this action
     */
    public GenerateScriptsAction(ListComponent target) {
        this(target, WindowManager.OpenType.THIS_TAB, ACTION_ID);
    }

    /**
     * Constructor that allows to specify the action name and how the editor screen opens.
     * @param target    component containing this action
     * @param openType  how to open the editor screen
     * @param id        action name
     */
    public GenerateScriptsAction(ListComponent target, WindowManager.OpenType openType, String id) {
        super(id);

        this.target = target;
        this.openType = openType;

        Configuration configuration = AppBeans.get(Configuration.NAME);
        ClientConfig config = configuration.getConfig(ClientConfig.class);
        setShortcut(config.getTableEditShortcut());
    }

    /**
     * This method is invoked by the action owner component.
     *
     * @param component component invoking the action
     */
    @Override
    public void actionPerform(Component component) {
        final Set<Entity> selected = target.getSelected();
        target.getFrame().openWindow("admintools$generateScriptsDialog",
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("selectedEntities", selected)
        );
    }

    @Override
    public String getCaption() {
        return super.getCaption();
    }
}
