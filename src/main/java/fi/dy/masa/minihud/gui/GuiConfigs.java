package fi.dy.masa.minihud.gui;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigTypeWrapper;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.minihud.Reference;
import fi.dy.masa.minihud.config.Configs;
import fi.dy.masa.minihud.config.InfoToggle;
import fi.dy.masa.minihud.config.RendererToggle;
import fi.dy.masa.minihud.config.StructureToggle;
public class GuiConfigs extends GuiConfigsBase
{
    public static ArrayList<ConfigGuiTab> tabs = new ArrayList<>();

    public static ConfigGuiTab tab;

    static
    {
        addOptionsFromList("minihud.gui.button.config_gui.generic", Configs.Generic.OPTIONS, 200);

        addOptionsFromList("minihud.gui.button.config_gui.colors", Configs.Colors.OPTIONS, 100);

        addOptionsFromList("minihud.gui.button.config_gui.formats", Configs.Formats.OPTIONS, 400);

        addOptionsFromList("minihud.gui.button.config_gui.info_lines", (ImmutableList) ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(InfoToggle.values())), 100);
        addOptionsFromList("minihud.gui.button.config_gui.info_lines", (ImmutableList) ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(InfoToggle.values())), 100);
        addOptionsFromList("minihud.gui.button.config_gui.info_lines", (ImmutableList) ConfigUtils.createConfigWrapperForType(ConfigType.INTEGER, ImmutableList.copyOf(InfoToggle.values())), 100);

        addOptionsFromList("minihud.gui.button.config_gui.structures", ImmutableList.of(new ConfigTypeWrapper(ConfigType.BOOLEAN, RendererToggle.OVERLAY_STRUCTURE_MAIN_TOGGLE)), 200);
        for(IConfigBase option : StructureToggle.getToggleConfigs())
            addOptionsFromList("minihud.gui.button.config_gui.structures", ImmutableList.of(option), 200);
        for(IConfigBase option : StructureToggle.getHotkeys())
            addOptionsFromList("minihud.gui.button.config_gui.structures", ImmutableList.of(option), 200);
        for(IConfigBase option : StructureToggle.getColorConfigs())
            addOptionsFromList("minihud.gui.button.config_gui.structures", ImmutableList.of(option), 200);

        addOptionsFromList("minihud.gui.button.config_gui.renderers", (ImmutableList) ConfigUtils.createConfigWrapperForType(ConfigType.BOOLEAN, ImmutableList.copyOf(RendererToggle.values())), 220);
        addOptionsFromList("minihud.gui.button.config_gui.renderers", (ImmutableList) ConfigUtils.createConfigWrapperForType(ConfigType.HOTKEY, ImmutableList.copyOf(RendererToggle.values())), 220);

        addOptionsFromList("minihud.gui.button.config_gui.shapes", ImmutableList.<IConfigBase>of(), 100);

        tab = GuiConfigs.tabs.get(2);
    }

    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, "minihud.gui.title.configs");

    }

    public static void addOptionsFromList(String translationKey, ImmutableList<IConfigBase> OPTIONS, int width)
    {
        for(ConfigGuiTab tab : GuiConfigs.tabs)
            if(tab.sameTab(translationKey))
            {
                tab.options.addAll(OPTIONS);
                return;
            }

        GuiConfigs.tabs.add(new GuiConfigs.ConfigGuiTab(translationKey, OPTIONS, width));
    }


    public static ConfigGuiTab getConfigGuiTab(String translationKey)
    {
        for(ConfigGuiTab tab : GuiConfigs.tabs)
            if(tab.sameTab(translationKey))
                return tab;
        return null;
    }

    @Override
    public void initGui()
    {
        if (GuiConfigs.tab.sameTab("minihud.gui.button.config_gui.shapes"))
        {
            GuiBase.openGui(new GuiShapeManager());
            return;
        }

        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        int rows = 1;

        for (ConfigGuiTab tab : GuiConfigs.tabs)
        {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10)
            {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createButton(x, y, width, tab);
        }

        if (rows > 1)
        {
            int scrollbarPosition = this.getListWidget().getScrollbar().getValue();
            this.setListPosition(this.getListX(), 50 + (rows - 1) * 22);
            this.reCreateListWidget();
            this.getListWidget().getScrollbar().setValue(scrollbarPosition);
            this.getListWidget().refreshEntries();
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfigs.tab != tab);
        this.addButton(button, new ButtonListenerConfigTabs(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth()
    {
        return GuiConfigs.tab.getWidth();
    }

    @Override
    protected boolean useKeybindSearch()
    {
        // return GuiConfigs.tab == ConfigGuiTab.INFO_LINES || GuiConfigs.tab == ConfigGuiTab.RENDERERS;
        return true;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        return ConfigOptionWrapper.createFor(GuiConfigs.tab.options);
    }

    private static class ButtonListenerConfigTabs implements IButtonActionListener
    {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListenerConfigTabs(ConfigGuiTab tab, GuiConfigs parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            GuiConfigs.tab = this.tab;

            if (this.tab.sameTab("minihud.gui.button.config_gui.shapes"))
            {
                GuiBase.openGui(new GuiShapeManager());
            }
            else
            {
                this.parent.reCreateListWidget(); // apply the new config width
                this.parent.getListWidget().resetScrollbarPosition();
                this.parent.initGui();
            }
        }
    }

    public static class ConfigGuiTab
    {
        public ArrayList<IConfigBase> options;

        private final String translationKey;
        
        private final int width;

        ConfigGuiTab(String translationKey, ImmutableList<IConfigBase> OPTIONS, int width)
        {
            this.translationKey = translationKey;
            this.options = new ArrayList<IConfigBase>(OPTIONS);
            this.width = width;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        public boolean sameTab(String translationKey)
        {
          return this.translationKey == translationKey;
        }

        public int getWidth()
        {
          return this.width;
        }
    }
}
