package fi.dy.masa.minihud.config;

import java.io.File;
import java.util.List;

import javax.management.ImmutableDescriptor;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.minihud.Reference;
import fi.dy.masa.minihud.util.BlockGridMode;
import fi.dy.masa.minihud.util.LightLevelMarkerMode;
import fi.dy.masa.minihud.util.LightLevelNumberMode;

public class Configs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";
    private static final int CONFIG_VERSION = 1;

    public static class Generic
    {
        public static final ConfigBoolean       AXOLOTL_TOOLTIPS                    = new ConfigBoolean("axolotlTooltipsTooltips", false, "Adds the Axolotl variant name to the bucket tooltip");
        public static final ConfigBoolean       BEE_TOOLTIPS                        = new ConfigBoolean("beeTooltips", false, "Adds the number of contained bees to the tooltip of Bee Hive and Bee Nest items");
        public static final ConfigBoolean       HONEY_TOOLTIPS                      = new ConfigBoolean("honeyTooltips", false, "Adds the honey level to the tooltip of Bee Hive and Bee Nest items");
        public static final ConfigOptionList    BLOCK_GRID_OVERLAY_MODE             = new ConfigOptionList("blockGridOverlayMode", BlockGridMode.ALL, "The block grid render mode");
        public static final ConfigInteger       BLOCK_GRID_OVERLAY_RADIUS           = new ConfigInteger("blockGridOverlayRadius", 32, "The radius of the block grid lines to render");
        //public static final ConfigString        COORDINATE_FORMAT_STRING            = new ConfigString("coordinateFormat", "x: %.1f y: %.1f z: %.1f", "The format string for the coordinate line.\nNeeds to have three %f format strings!\nDefault: x: %.1f y: %.1f z: %.1f");
        //public static final ConfigString        DATE_FORMAT_REAL                    = new ConfigString("dateFormatReal", "yyyy-MM-dd HH:mm:ss", "The format string for real time, see the Java SimpleDateFormat\nclass for the format patterns, if needed.");
        //public static final ConfigString        DATE_FORMAT_MINECRAFT               = new ConfigString("dateFormatMinecraft", "MC time: (day {DAY}) {HOUR}:{MIN}:xx", "The format string for the Minecraft time.\nThe supported placeholders are: {DAY_1}, {DAY}, {HOUR}, {MIN}, {SEC}.\n{DAY_1} starts the day counter from 1, {DAY} starts from 0.");
        public static final ConfigBoolean       DEBUG_MESSAGES                      = new ConfigBoolean("debugMessages", false, "Enables some debug messages in the game console");
        public static final ConfigBoolean       DEBUG_RENDERER_PATH_MAX_DIST        = new ConfigBoolean("debugRendererPathFindingEnablePointWidth", true, "If true, then the vanilla pathfinding debug renderer\nwill render the path point width boxes.");
        public static final ConfigBoolean       DONT_RESET_SEED_ON_DIMENSION_CHANGE = new ConfigBoolean("dontClearStoredSeedOnDimensionChange", true, "Don't clear the stored world seed when just changing dimensions.\nSome mods may use per-dimension seeds, so you may need to change\nthis in case the different dimensions on your server/mod pack\nhave different world seeds.");
        public static final ConfigBoolean       ENABLED                             = new ConfigBoolean("enabled", true, "The main rendering toggle for all MiniHUD rendering");
        public static final ConfigBoolean       FIX_VANILLA_DEBUG_RENDERERS         = new ConfigBoolean("enableVanillaDebugRendererFix", true, "If true, then the vanilla debug renderer OpenGL state is fixed.");
        public static final ConfigDouble        FONT_SCALE                          = new ConfigDouble("fontScale", 0.5, 0.0, 100.0, "Font scale factor for the info line HUD. Default: 0.5\n");
        public static final ConfigOptionList    HUD_ALIGNMENT                       = new ConfigOptionList("hudAlignment", HudAlignment.TOP_LEFT, "The alignment of the info line HUD");
        public static final ConfigBoolean       LIGHT_LEVEL_COLORED_NUMBERS         = new ConfigBoolean("lightLevelColoredNumbers", true, "Whether to use colored or white numbers\nfor the Light Level overlay numbers");
        public static final ConfigOptionList    LIGHT_LEVEL_MARKER_MODE             = new ConfigOptionList("lightLevelMarkers", LightLevelMarkerMode.SQUARE, "Which type of colored marker to use in the\nLight Level overlay, if any");
        public static final ConfigDouble        LIGHT_LEVEL_MARKER_SIZE             = new ConfigDouble("lightLevelMarkerSize", 0.84, 0.0, 1.0, "The size of the light level colored marker.\nRange: 0.0 - 1.0");
        public static final ConfigOptionList    LIGHT_LEVEL_NUMBER_MODE             = new ConfigOptionList("lightLevelNumbers", LightLevelNumberMode.BLOCK, "Which light level number(s) to render in the Light Level overlay");
        public static final ConfigDouble        LIGHT_LEVEL_NUMBER_OFFSET_BLOCK_X   = new ConfigDouble("lightLevelNumberOffsetBlockX", 0.26, 0.0, 1.0, "The relative \"x\" offset for the block light level number.\nRange: 0.0 - 1.0");
        public static final ConfigDouble        LIGHT_LEVEL_NUMBER_OFFSET_BLOCK_Y   = new ConfigDouble("lightLevelNumberOffsetBlockY", 0.32, 0.0, 1.0, "The relative \"y\" offset for the block light level number.\nRange: 0.0 - 1.0");
        public static final ConfigDouble        LIGHT_LEVEL_NUMBER_OFFSET_SKY_X     = new ConfigDouble("lightLevelNumberOffsetSkyX", 0.42, 0.0, 1.0, "The relative \"x\" offset for the sky light level number.\nRange: 0.0 - 1.0");
        public static final ConfigDouble        LIGHT_LEVEL_NUMBER_OFFSET_SKY_Y     = new ConfigDouble("lightLevelNumberOffsetSkyY", 0.56, 0.0, 1.0, "The relative \"y\" offset for the sky light level number.\nRange: 0.0 - 1.0");
        public static final ConfigBoolean       LIGHT_LEVEL_NUMBER_ROTATION         = new ConfigBoolean("lightLevelNumberRotation", true, "If true, then the light level numbers will rotate\naccording to the player's current facing");
        public static final ConfigInteger       LIGHT_LEVEL_RANGE                   = new ConfigInteger("lightLevelRange", 24, 1, 64, "The block range to render the Light Level overlay in");
        public static final ConfigInteger       LIGHT_LEVEL_THRESHOLD               = new ConfigInteger("lightLevelThreshold", 8, 0, 15, "The light level threshold which is considered safe");
        public static final ConfigDouble        LIGHT_LEVEL_Z_OFFSET                = new ConfigDouble("lightLevelZOffset", 0.005, 0.0, 1.0, "The relative \"z\" offset for the light level overlay.\nMeant to help with potential z-fighting issues.\nRange: 0.0 - 1.0");
        public static final ConfigBoolean       MAP_PREVIEW                         = new ConfigBoolean("mapPreview", false, "Enables rendering a preview of the map,\nwhen you hold shift while hovering over a map item");
        public static final ConfigInteger       MAP_PREVIEW_SIZE                    = new ConfigInteger("mapPreviewSize", 160, 16, 512, "The size of the rendered map previews");
        public static final ConfigHotkey        OPEN_CONFIG_GUI                     = new ConfigHotkey("openConfigGui", "H,C", "A hotkey to open the in-game Config GUI");
        public static final ConfigBoolean       REQUIRE_SNEAK                       = new ConfigBoolean("requireSneak", false, "Require the player to be sneaking to render the info line HUD");
        public static final ConfigHotkey        REQUIRED_KEY                        = new ConfigHotkey("requiredKey", "", KeybindSettings.MODIFIER_INGAME_EMPTY, "Require holding this key to render the HUD");
        public static final ConfigHotkey        SET_DISTANCE_REFERENCE_POINT        = new ConfigHotkey("setDistanceReferencePoint", "", "A hotkey to store the player's current position\nas the reference point for the distance info line type");
        public static final ConfigHotkey        SHAPE_EDITOR                        = new ConfigHotkey("shapeEditor", "", "Opens the Shape Editor GUI for the selected shape");
        public static final ConfigBoolean       SHULKER_BOX_PREVIEW                 = new ConfigBoolean("shulkerBoxPreview", false, "Enables rendering a preview of the Shulker Box contents,\nwhen you hold shift while hovering over a Shulker Box item");
        public static final ConfigBoolean       SHULKER_DISPLAY_BACKGROUND_COLOR    = new ConfigBoolean("shulkerDisplayBgColor", true, "Enables tinting/coloring the Shulker Box display\nbackground texture with the dye color of the box");
        public static final ConfigBoolean       SHULKER_DISPLAY_REQUIRE_SHIFT       = new ConfigBoolean("shulkerDisplayRequireShift", true, "Whether or not holding shift is required for the Shulker Box preview");
        public static final ConfigInteger       SLIME_CHUNK_OVERLAY_RADIUS          = new ConfigInteger("slimeChunkOverlayRadius", -1, -1, 40, "The radius of chunks to render the slime chunk overlay in.\nValid range: -1 ... 40, where -1 = render distance");
        public static final ConfigBoolean       SORT_LINES_BY_LENGTH                = new ConfigBoolean("sortLinesByLength", false, "Sort the lines by their text's length");
        public static final ConfigBoolean       SORT_LINES_REVERSED                 = new ConfigBoolean("sortLinesReversed", false, "Reverse the line sorting order");
        public static final ConfigInteger       SPAWNABLE_COLUMNS_OVERLAY_RADIUS    = new ConfigInteger("spawnableColumnHeightsOverlayRadius", 40, 0, 128, "The radius (in blocks) to render the spawnable\ncolumn heights overlay in. Valid range: 0 - 128");
        public static final ConfigBoolean       STRUCTURES_RENDER_THROUGH           = new ConfigBoolean("structuresRenderThrough", false, "If enabled, then the Structure Bounding Boxes\nwill be rendered through blocks");
        public static final ConfigInteger       TEXT_POS_X                          = new ConfigInteger("textPosX", 4, "Text X position from the screen edge (default: 4)");
        public static final ConfigInteger       TEXT_POS_Y                          = new ConfigInteger("textPosY", 4, "Text Y position from the screen edge (default: 4)");
        public static final ConfigInteger       TIME_DAY_DIVISOR                    = new ConfigInteger("timeDayDivisor", 24000, 1, Integer.MAX_VALUE, "The divisor value for the modulo of the day time");
        public static final ConfigInteger       TIME_TOTAL_DIVISOR                  = new ConfigInteger("timeTotalDivisor", 24000, 1, Integer.MAX_VALUE, "The divisor value for the modulo of the total world time");
        public static final ConfigHotkey        TOGGLE_KEY                          = new ConfigHotkey("toggleKey", "H", KeybindSettings.RELEASE_EXCLUSIVE, "The main toggle key");
        //public static final ConfigBoolean       USE_CUSTOMIZED_COORDINATES          = new ConfigBoolean("useCustomizedCoordinateFormat", true, "Use the customized coordinate format string");
        public static final ConfigBoolean       USE_FONT_SHADOW                     = new ConfigBoolean("useFontShadow", false, "Use font shadow");
        public static final ConfigBoolean       USE_TEXT_BACKGROUND                 = new ConfigBoolean("useTextBackground", true, "Use a solid background color behind the text");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                AXOLOTL_TOOLTIPS,
                BEE_TOOLTIPS,
                HONEY_TOOLTIPS,
                ENABLED,
                DEBUG_MESSAGES,
                DEBUG_RENDERER_PATH_MAX_DIST,
                DONT_RESET_SEED_ON_DIMENSION_CHANGE,
                FIX_VANILLA_DEBUG_RENDERERS,
                LIGHT_LEVEL_COLORED_NUMBERS,
                LIGHT_LEVEL_NUMBER_ROTATION,
                MAP_PREVIEW,
                REQUIRE_SNEAK,
                SHULKER_BOX_PREVIEW,
                SHULKER_DISPLAY_BACKGROUND_COLOR,
                SHULKER_DISPLAY_REQUIRE_SHIFT,
                SORT_LINES_BY_LENGTH,
                SORT_LINES_REVERSED,
                STRUCTURES_RENDER_THROUGH,
                //USE_CUSTOMIZED_COORDINATES,
                USE_FONT_SHADOW,
                USE_TEXT_BACKGROUND,

                OPEN_CONFIG_GUI,
                REQUIRED_KEY,
                SET_DISTANCE_REFERENCE_POINT,
                SHAPE_EDITOR,
                TOGGLE_KEY,

                BLOCK_GRID_OVERLAY_MODE,
                LIGHT_LEVEL_MARKER_MODE,
                LIGHT_LEVEL_NUMBER_MODE,
                HUD_ALIGNMENT,

                BLOCK_GRID_OVERLAY_RADIUS,
                //COORDINATE_FORMAT_STRING,
                //DATE_FORMAT_REAL,
                //DATE_FORMAT_MINECRAFT,
                FONT_SCALE,
                LIGHT_LEVEL_MARKER_SIZE,
                LIGHT_LEVEL_NUMBER_OFFSET_BLOCK_X,
                LIGHT_LEVEL_NUMBER_OFFSET_BLOCK_Y,
                LIGHT_LEVEL_NUMBER_OFFSET_SKY_X,
                LIGHT_LEVEL_NUMBER_OFFSET_SKY_Y,
                LIGHT_LEVEL_RANGE,
                LIGHT_LEVEL_THRESHOLD,
                LIGHT_LEVEL_Z_OFFSET,
                MAP_PREVIEW_SIZE,
                SLIME_CHUNK_OVERLAY_RADIUS,
                SPAWNABLE_COLUMNS_OVERLAY_RADIUS,
                TEXT_POS_X,
                TEXT_POS_Y,
                TIME_DAY_DIVISOR,
                TIME_TOTAL_DIVISOR
        );

        public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
                TOGGLE_KEY,
                REQUIRED_KEY,
                OPEN_CONFIG_GUI,
                SET_DISTANCE_REFERENCE_POINT,
                SHAPE_EDITOR
        );
    }

    public static class Colors
    {
        public static final ConfigColor COLORFG                                 = new ConfigColor("ColorFG",                            "#00F8F8F2", "Variable color FG");
        public static final ConfigColor COLORBG                                 = new ConfigColor("ColorBG",                            "#00282A36", "Variable color BG");
        public static final ConfigColor COLOR0                                  = new ConfigColor("Color0",                             "#00000000", "Variable color 0");
        public static final ConfigColor COLOR1                                  = new ConfigColor("Color1",                             "#00FF5555", "Variable color 1");
        public static final ConfigColor COLOR2                                  = new ConfigColor("Color2",                             "#0050FA7B", "Variable color 2");
        public static final ConfigColor COLOR3                                  = new ConfigColor("Color3",                             "#00F1FA8C", "Variable color 3");
        public static final ConfigColor COLOR4                                  = new ConfigColor("Color4",                             "#00BD93F9", "Variable color 4");
        public static final ConfigColor COLOR5                                  = new ConfigColor("Color5",                             "#00FF79C6", "Variable color 5");
        public static final ConfigColor COLOR6                                  = new ConfigColor("Color6",                             "#008BE9FD", "Variable color 6");
        public static final ConfigColor COLOR7                                  = new ConfigColor("Color7",                             "#00BFBFBF", "Variable color 7");
        public static final ConfigColor COLOR8                                  = new ConfigColor("Color8",                             "#004D4D4D", "Variable color 8");
        public static final ConfigColor COLOR9                                  = new ConfigColor("Color9",                             "#00FF6E67", "Variable color 9");
        public static final ConfigColor COLOR10                                 = new ConfigColor("Color10",                            "#005AF78E", "Variable color 10");
        public static final ConfigColor COLOR11                                 = new ConfigColor("Color11",                            "#00F4F99D", "Variable color 11");
        public static final ConfigColor COLOR12                                 = new ConfigColor("Color12",                            "#00CAA9FA", "Variable color 12");
        public static final ConfigColor COLOR13                                 = new ConfigColor("Color13",                            "#00FF92D0", "Variable color 13");
        public static final ConfigColor COLOR14                                 = new ConfigColor("Color14",                            "#009AEDFE", "Variable color 14");
        public static final ConfigColor COLOR15                                 = new ConfigColor("Color15",                            "#00E6E6E6", "Variable color 15");
        public static final ConfigColor BLOCK_GRID_OVERLAY_COLOR                = new ConfigColor("blockGridOverlayColor",              "#80FFFFFF", "Color for the block grid overlay");
        public static final ConfigColor LIGHT_LEVEL_MARKER_DARK                 = new ConfigColor("lightLevelMarkerDark",               "#FFFF4848", "The color for the spawnable spots marker");
        public static final ConfigColor LIGHT_LEVEL_MARKER_LIT                  = new ConfigColor("lightLevelMarkerLit",                "#FFFFFF33", "The color for the safe (during day) spots marker");
        public static final ConfigColor LIGHT_LEVEL_NUMBER_BLOCK_DARK           = new ConfigColor("lightLevelNumberBlockDark",          "#FFC03030", "The color for the spawnable spots number of the block light value");
        public static final ConfigColor LIGHT_LEVEL_NUMBER_BLOCK_LIT            = new ConfigColor("lightLevelNumberBlockLit",           "#FF209040", "The color for the safe spots number of the block light value");
        public static final ConfigColor LIGHT_LEVEL_NUMBER_SKY_DARK             = new ConfigColor("lightLevelNumberSkyDark",            "#FFFFF030", "The color for the spawnable spots number of the sky light value");
        public static final ConfigColor LIGHT_LEVEL_NUMBER_SKY_LIT              = new ConfigColor("lightLevelNumberSkyLit",             "#FF40E0FF", "The color for the safe spots number of the sky light value");
        public static final ConfigColor RANDOM_TICKS_FIXED_OVERLAY_COLOR        = new ConfigColor("randomTicksFixedOverlayColor",       "#30F9F225", "Color for the fixed-point random ticked chunks overlay");
        public static final ConfigColor RANDOM_TICKS_PLAYER_OVERLAY_COLOR       = new ConfigColor("randomTicksPlayerOverlayColor",      "#3030FE73", "Color for the player-following random ticked chunks overlay");
        public static final ConfigColor REGION_OVERLAY_COLOR                    = new ConfigColor("regionOverlayColor",                 "#30FF8019", "Color for the region file overlay");
        public static final ConfigColor SHAPE_CAN_DESPAWN_SPHERE                = new ConfigColor("shapeCanDespawnSphere",              "#60A04050", "Default color for the \"Can Despawn Sphere\" overlay.\nThe color can be changed for each sphere via its configuration GUI.");
        public static final ConfigColor SHAPE_CAN_SPAWN_SPHERE                  = new ConfigColor("shapeCanSpawnSphere",                "#60A04050", "Default color for the \"Can Spawn Sphere\" overlay.\nThe color can be changed for each sphere via its configuration GUI.");
        public static final ConfigColor SHAPE_CIRCLE                            = new ConfigColor("shapeCircle",                        "#6030B0B0", "Default color for the Circle renderer.\nThe color can be changed for each shape via its configuration GUI.");
        public static final ConfigColor SHAPE_DESPAWN_SPHERE                    = new ConfigColor("shapeDespawnSphere",                 "#60A04050", "Default color for the \"Despawn Sphere\" overlay.\nThe color can be changed for each sphere via its configuration GUI.");
        public static final ConfigColor SHAPE_SPHERE_BLOCKY                     = new ConfigColor("shapeSphereBlocky",                  "#6030B0B0", "Default color for the blocky/block-based Sphere renderer.\nThe color can be changed for each sphere via its configuration GUI.");
        public static final ConfigColor SLIME_CHUNKS_OVERLAY_COLOR              = new ConfigColor("slimeChunksOverlayColor",            "#3020F020", "Color for the slime chunks overlay");
        public static final ConfigColor SPAWN_PLAYER_ENTITY_OVERLAY_COLOR       = new ConfigColor("spawnPlayerEntityOverlayColor",      "#302050D0", "Color for the entity-processing would-be spawn chunks overlay of\nhow the spawn chunks would be if the spawn were\nto be at the player's current position");
        public static final ConfigColor SPAWN_PLAYER_LAZY_OVERLAY_COLOR         = new ConfigColor("spawnPlayerLazyOverlayColor",        "#30D030D0", "Color for the \"lazy-loaded\" would-be spawn chunks overlay of\nhow the spawn chunks would be if the spawn were\nto be at the player's current position");
        public static final ConfigColor SPAWN_PLAYER_OUTER_OVERLAY_COLOR        = new ConfigColor("spawnPlayerOuterOverlayColor",       "#306900D2", "Color for the 1.14+ outer loaded would-be spawn chunks overlay of\nhow the spawn chunks would be if the spawn were\nto be at the player's current position");
        public static final ConfigColor SPAWN_REAL_ENTITY_OVERLAY_COLOR         = new ConfigColor("spawnRealEntityOverlayColor",        "#3030FF20", "Color for the entity-processing real spawn chunks overlay");
        public static final ConfigColor SPAWN_REAL_LAZY_OVERLAY_COLOR           = new ConfigColor("spawnRealLazyOverlayColor",          "#30FF3020", "Color for the \"lazy-loaded\" real spawn chunks overlay");
        public static final ConfigColor SPAWN_REAL_OUTER_OVERLAY_COLOR          = new ConfigColor("spawnRealOuterOverlayColor",         "#309D581A", "Color for the 1.14+ outer loaded real spawn chunks overlay");
        public static final ConfigColor SPAWNABLE_COLUMNS_OVERLAY_COLOR         = new ConfigColor("spawnableColumnHeightsOverlayColor", "#A0FF00FF", "Color for the spawnable sub-chunks overlay");
        public static final ConfigColor TEXT_BACKGROUND_COLOR                   = new ConfigColor("textBackgroundColor",                "#A0505050", "Text background color");
        public static final ConfigColor TEXT_COLOR                              = new ConfigColor("textColor",                          "#FFE0E0E0", "Info line text color");

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                BLOCK_GRID_OVERLAY_COLOR,
                LIGHT_LEVEL_MARKER_DARK,
                LIGHT_LEVEL_MARKER_LIT,
                LIGHT_LEVEL_NUMBER_BLOCK_DARK,
                LIGHT_LEVEL_NUMBER_BLOCK_LIT,
                LIGHT_LEVEL_NUMBER_SKY_DARK,
                LIGHT_LEVEL_NUMBER_SKY_LIT,
                RANDOM_TICKS_FIXED_OVERLAY_COLOR,
                RANDOM_TICKS_PLAYER_OVERLAY_COLOR,
                REGION_OVERLAY_COLOR,
                SHAPE_CAN_DESPAWN_SPHERE,
                SHAPE_CAN_SPAWN_SPHERE,
                SHAPE_CIRCLE,
                SHAPE_DESPAWN_SPHERE,
                SHAPE_SPHERE_BLOCKY,
                SLIME_CHUNKS_OVERLAY_COLOR,
                SPAWN_PLAYER_ENTITY_OVERLAY_COLOR,
                SPAWN_PLAYER_LAZY_OVERLAY_COLOR,
                SPAWN_PLAYER_OUTER_OVERLAY_COLOR,
                SPAWN_REAL_ENTITY_OVERLAY_COLOR,
                SPAWN_REAL_LAZY_OVERLAY_COLOR,
                SPAWN_REAL_OUTER_OVERLAY_COLOR,
                SPAWNABLE_COLUMNS_OVERLAY_COLOR,
                TEXT_BACKGROUND_COLOR,
                TEXT_COLOR,
                COLORFG,
                COLORBG,
                COLOR0,
                COLOR1,
                COLOR2,
                COLOR3,
                COLOR4,
                COLOR5,
                COLOR6,
                COLOR7,
                COLOR8,
                COLOR9,
                COLOR10,
                COLOR11,
                COLOR12,
                COLOR13,
                COLOR14,
                COLOR15
        );
    }

    public static class Formats
    {
      public static final ConfigString
        BEE_COUNT_FORMAT                 = new ConfigString("infoBeeCountFormat", "[{\"color\":\"white\",\"text\":\"" +
        "Bees: \"}, {\"color\":\"aqua\",\"text\":\"{bees}\"}]", "Format of infoBeeCount"),
        BIOME_FORMAT                     = new ConfigString("infoBiomeFormat", "{\"color\":\"white\",\"text\":\"" +
        "Biome: {biome}\"}", "Format of infoBiome"),
        BIOME_REG_NAME_FORMAT            = new ConfigString("infoBiomeRegistryNameFormat", "{\"color\":\"white\",\"text\":\"" +
        "Biome reg name: {name}\"}", "Format of infoBiomeRegistryName"),
        BLOCK_BREAK_SPEED_FORMAT         = new ConfigString("infoBlockBreakSpeedFormat", "{\"color\":\"white\",\"text\":\"" +
        "BBS: {bbs}.2f\"}", "Format of infoBlockBreakSpeed"),
        BLOCK_IN_CHUNK_FORMAT            = new ConfigString("infoBlockInChunkFormat", "{\"color\":\"white\",\"text\":\"" +
        "Block: {x}d, {y}d, {z}d within Sub-Chunk: {cx}d, {cy}d, {cz}d\"}", "Format of infoBlockInChunk"),
        BLOCK_POS_FORMAT                 = new ConfigString("infoBlockPositionFormat", "{\"color\":\"white\",\"text\":\"" +
        "Block: {x}d, {y}d, {z}d\"}", "Format of infoBlockPosition"),
        BLOCK_PROPS_SEPARATOR_FORMAT     = new ConfigString("infoBlockPropertiesSeparatorFormat", "{\"color\":\"white\",\"text\":\"" +
        ":\"}", "Format of the separator used in infoBlockProperties"),
        BLOCK_PROPS_BOOLEAN_TRUE_FORMAT  = new ConfigString("infoBlockPropertiesBooleanTrueFormat", "[{\"color\":\"white\",\"text\":\"" +
        "{prop}\"}, {separator}, {\"color\":\"green\",\"text\":\"TRUE\"}]", "Format of boolean properties in infoBlockProperties when they're true"),
        BLOCK_PROPS_BOOLEAN_FALSE_FORMAT = new ConfigString("infoBlockPropertiesBooleanFalseFormat", "[{\"color\":\"white\",\"text\":\"" +
        "{prop}\"}, {separator}, {\"color\":\"red\",\"text\":\"FALSE\"}]", "Format of boolean properties in infoBlockProperties when they're false"),
        BLOCK_PROPS_DIRECTION_FORMAT     = new ConfigString("infoBlockPropertiesDirectionFormat", "[{\"color\":\"white\",\"text\":\"" +
        "{prop}\"}, {separator}, {\"color\":\"gold\",\"text\":\"{value}\"}]", "Format of direction properties in infoBlockProperties"),
        BLOCK_PROPS_INT_FORMAT           = new ConfigString("infoBlockPropertiesIntFormat", "[{\"color\":\"white\",\"text\":\"" +
        "{prop}\"}, {separator}, {\"color\":\"aqua\",\"text\":\"{value}\"}]", "Format of int properties in infoBlockProperties"),
        BLOCK_PROPS_STRING_FORMAT        = new ConfigString("infoBlockPropertiesStringFormat", "[{\"color\":\"white\",\"text\":\"" +
        "{prop}\"}, {separator}, {\"color\":\"white\",\"text\":\"{value}\"}]", "Format of string properties in infoBlockProperties"),
        BLOCK_PROPS_HEADING_FORMAT       = new ConfigString("infoBlockPropertiesHeadingFormat", "{\"color\":\"white\",\"text\":\"" +
        "{name}\"}", "Format of the heading of infoBlockProperties"),
        CHUNK_POS_FORMAT                 = new ConfigString("infoChunkPositionFormat", "{\"color\":\"white\",\"text\":\"" +
        " / Sub-Chunk: {x}d, {y}d, {z}d\"}", "Format of infoChunkPosition"),
        CHUNK_SECTIONS_FORMAT            = new ConfigString("infoChunkSectionsFormat", "{\"color\":\"white\",\"text\":\"" +
        "C: {c}d\"}", "Format of infoChunkSections"),
        CHUNK_SECTIONS_FULL_FORMAT       = new ConfigString("infoChunkSectionsLineFormat", "{\"color\":\"white\",\"text\":\"" +
        "{c}\"}", "Format of infoChunkSectionsLine"),
        CHUNK_UPDATES_FORMAT             = new ConfigString("infoChunkUpdatesFormat", "{\"color\":\"white\",\"text\":\"" +
        "\"}", "Format of infoChunkUpdates"),
        COORDINATES_FORMAT               = new ConfigString("infoCoordinatesFormat", "{\"color\":\"white\",\"text\":\"" +
        "XYZ: {x}.2f / {y}.4f / {z}.2f\"}", "Format of infoCoordinates, change precision by changing the numbers"),
        DIFFICULTY_FORMAT                = new ConfigString("infoDifficultyFormat", "{\"color\":\"white\",\"text\":\"" +
        "Local Difficulty: {local}.2f // {clamped}.2f (Day {day}d)\"}", "Format of infoDifficulty"),
        DIMENSION_FORMAT                 = new ConfigString("infoDimensionIdFormat", "{\"color\":\"white\",\"text\":\"" +
        " / dim: {dim}\"}", "Format of infoDimensionId"),
        DISTANCE_FORMAT                  = new ConfigString("infoDistanceFormat", "{\"color\":\"white\",\"text\":\"" +
        "Distance: {d}.2f (x: {dx}.2f y: {dy}.2f z: {dz}.2f) [to x: {rx}.2f y: {ry}.2f z: {rz}.2f]\"}", "Format of infoDistance"),
        ENTITIES_CLIENT_FORMAT           = new ConfigString("infoEntitiesClientFormat", "{\"color\":\"white\",\"text\":\"" +
        "Entities - Client: {e}d\"}", "Format of infoEntitiesClient"),
        ENTITIES_SERVER_FORMAT           = new ConfigString("infoEntitiesServerFormat", "{\"color\":\"white\",\"text\":\"" +
        ", Server: {e}d\"}", "Format of infoEntitiesServer"),
        ENTITY_REG_NAME_FORMAT           = new ConfigString("infoEntityRegistryNameFormat", "{\"color\":\"white\",\"text\":\"" +
        "Entity reg name: {name}s\"}", "Format of infoEntityRegistryName"),
        FACING_FORMAT                    = new ConfigString("infoFacingFormat", "[{\"color\":\"white\",\"text\":\"" +
        "Facing: {dir} (\"}, {coord}, {\"color\":\"white\",\"text\":\")\"}]", "Format of infoFacing"),
        FACING_PX_FORMAT                 = new ConfigString("infoFacingPosXFormat", "{\"color\":\"white\",\"text\":\"" +
        "Positive X\"}", "Text for infoFacing when facing positive X"),
        FACING_NX_FORMAT                 = new ConfigString("infoFacingNegXFormat", "{\"color\":\"white\",\"text\":\"" +
        "Negative X\"}", "Text for infoFacing when facing negative X"),
        FACING_PZ_FORMAT                 = new ConfigString("infoFacingPosZFormat", "{\"color\":\"white\",\"text\":\"" +
        "Positive Z\"}", "Text for infoFacing when facing positive Z"),
        FACING_NZ_FORMAT                 = new ConfigString("infoFacingNegZFormat", "{\"color\":\"white\",\"text\":\"" +
        "Negative Z\"}", "Text for infoFacing when facing negative Z"),
        FPS_FORMAT                       = new ConfigString("infoFPSFormat", "{\"color\":\"white\",\"text\":\"" +
        "{FPS}d fps\"}", "Format of infoFPS"),
        HONEY_LEVEL_FORMAT               = new ConfigString("infoHoneyLevelFormat", "[{\"color\":\"white\",\"text\":\"" +
        "Honey: \"}, {\"color\":\"aqua\",\"text\":\"{honey}\"}]", "Format of infoHoneyLevel"),
        LIGHT_LEVEL_CLIENT_FORMAT        = new ConfigString("infoLightLevelClientFormat", "{\"color\":\"white\",\"text\":\"" +
        "Client Light: {light}d (block: {block}d, sky: {sky}d)\"}", "Format of infoLightLevelClient"),
        LIGHT_LEVEL_SERVER_FORMAT        = new ConfigString("infoLightLevelServerFormat", "{\"color\":\"white\",\"text\":\"" +
        "Server Light: {light}d (block: {block}d, sky: {sky}d)\"}", "Format of infoLightLevelServer"),
        LOOKING_AT_BLOCK_FORMAT          = new ConfigString("infoLookingAtBlockFormat", "{\"color\":\"white\",\"text\":\"" +
        "Looking at block: {x}d, {y}d, {z}d\"}", "Format of infoLookingAtBlock"),
        LOOKING_AT_BLOCK_CHUNK_FORMAT    = new ConfigString("infoLookingAtBlockInChunkFormat", "{\"color\":\"white\",\"text\":\"" +
        " // Block: {x}d, {y}d, {z}d in Sub-Chunk: {cx}d, {cy}d, {cz}d\"}", "Format of infoLookingAtBlockInChunk"),
        LOOKING_AT_ENTITY_FORMAT         = new ConfigString("infoLookingAtEntityFormat", "{\"color\":\"white\",\"text\":\"" +
        "Entity: {entity}\"}", "Format of infoLookingAtEntity"),
        LOOKING_AT_ENTITY_LIVING_FORMAT  = new ConfigString("infoLookingAtEntityLivingFormat", "{\"color\":\"white\",\"text\":\"" +
        "Entity: {entity} - HP: {hp}.1f / {maxhp}.1f\"}", "Format of infoLookingAtEntity when entity is living"),
        MEMORY_USAGE_FORMAT              = new ConfigString("infoMemoryUsageFormat", "{\"color\":\"white\",\"text\":\"" +
        "Mem: {pused}2d% {used}03d/{max}03dMB | Allocated: {pallocated}2d% {total}03dMB\"}", "Format of infoMemoryUsage"),
        LOADED_CHUNKS_COUNT_SERVER_FORMAT= new ConfigString("infoLoadedChunksCountServerFormat", "{\"color\":\"white\",\"text\":\"" +
        "Server: {chunks}d / {total}d - Client: {client}\"}", "Format of infoLoadedChunksCount when playing singleplayer"),
        LOADED_CHUNKS_COUNT_CLIENT_FORMAT= new ConfigString("infoLoadedChunksCountClientFormat", "{\"color\":\"white\",\"text\":\"" +
        "{client}\"}", "Format of infoLoadedChunksCount when playing on a server"),
        PARTICLE_COUNT_FORMAT            = new ConfigString("infoParticleCountFormat", "{\"color\":\"white\",\"text\":\"" +
        "P: {p}\"}", "Format of infoParticleCount"),
        PING_FORMAT                      = new ConfigString("infoPingFormat", "{\"color\":\"white\",\"text\":\"" +
        "Ping: {ping}dms\"}", "Format of infoPing"),
        REGION_FILE_FORMAT               = new ConfigString("infoRegionFileFormat", "{\"color\":\"white\",\"text\":\"" +
        " / Region: r.{x}d.{z}d\"}", "Format of infoRegionFile"),
        ROTATION_PITCH_FORMAT            = new ConfigString("infoRotationPitchFormat", "{\"color\":\"white\",\"text\":\"" +
        " / Pitch: {pitch}.1f\"}", "Format of infoRotationPitch"),
        ROTATION_YAW_FORMAT              = new ConfigString("infoRotationYawFormat", "{\"color\":\"white\",\"text\":\"" +
        "Yaw: {yaw}.1f\"}", "Format of infoRotationYaw"),
        SERVER_TPS_VANILLA_FORMAT        = new ConfigString("infoServerTPSVanillaFormat", "{\"color\":\"white\",\"text\":\"" +
        "Server TPS: {preTps}{tps}.1f{rst} (MSPT [est]: {preMspt}{mspt}.1f{rst})\"}", "Format of infoServerTPS for vanilla servers"),
        SERVER_TPS_CARPET_FORMAT         = new ConfigString("infoServerTPSCarpetFormat", "{\"color\":\"white\",\"text\":\"" +
        "Server TPS: {preTps}{tps}.1f{rst} MSPT: {preMspt}{mspt}.1f{rst}\"}", "Format of infoServerTPS for carpet servers"),
        SERVER_TPS_NULL_FORMAT           = new ConfigString("infoServerTPSNullFormat", "{\"color\":\"white\",\"text\":\"" +
        "Server TPS: <no valid data>\"}", "Format of infoServerTPS when info is unavailable"),
        SLIME_CHUNK_FORMAT               = new ConfigString("infoSlimeChunkFormat", "[{\"color\":\"white\",\"text\":\"" +
        "Slime chunk: \"}, {result}]", "Format of infoSlimeChunk"),
        SLIME_CHUNK_YES_FORMAT           = new ConfigString("infoSlimeChunkYesFormat", "{\"color\":\"green\",\"text\":\"" +
        "YES\"}", "Format of the infoSlimeChunk result when it's positive"),
        SLIME_CHUNK_NO_FORMAT            = new ConfigString("infoSlimeChunkNoFormat", "{\"color\":\"red\",\"text\":\"" +
        "NO\"}", "Format of the infoSlimeChunk result when it's negative"),
        SLIME_CHUNK_NO_SEED_FORMAT       = new ConfigString("infoSlimeChunkNoSeedFormat", "{\"color\":\"white\",\"text\":\"" +
        "Slime chunk: <world seed not known>\"}", "Format of infoSlimeChunk when there's no seed"),
        SPEED_FORMAT                     = new ConfigString("infoSpeedFormat", "{\"color\":\"white\",\"text\":\"" +
        " / Speed: {speed}.3f m/s\"}", "Format of infoSpeed"),
        SPEED_AXIS_FORMAT                = new ConfigString("infoSpeedAxisFormat", "{\"color\":\"white\",\"text\":\"" +
        "Speed: x: {x}.3f y: {y}.3f z: {z}.3f m/s\"}", "Format of infoSpeedAxis"),
        TILE_ENTITIES_FORMAT             = new ConfigString("infoTileEntitiesFormat", "{\"color\":\"white\",\"text\":\"" +
        "Client world TE - L: {loaded}d, T: {ticking}d\"}", "Format of infoTileEntities"),
        TIME_DAY_MODULO_FORMAT           = new ConfigString("infoTimeDayModuloFormat", "{\"color\":\"white\",\"text\":\"" +
        "Day time % {mod}d: {time}5d\"}", "Format of infoTimeDayModulo"),
        TIME_REAL_FORMAT                 = new ConfigString("infoTimeIRLFormat", "{\"color\":\"white\",\"text\":\"" +
        "{yyyy}-{MM}-{dd} {HH}:{mm}:{ss}\"}", "The format string for real time, see the Java SimpleDateFormat\nclass for the format patterns, if needed."),
        TIME_TOTAL_MODULO_FORMAT         = new ConfigString("infoTimeTotalModuloFormat", "{\"color\":\"white\",\"text\":\"" +
        "Total time % {mod}d: {time}5d\"}", "Format of infoTimeTotalModulo"),
        TIME_WORLD_FORMAT                = new ConfigString("infoTimeWorldFormat", "{\"color\":\"white\",\"text\":\"" +
        "World time: {time}5d - total: {total}d\"}", "Format of infoTimeWorld"),
        TIME_WORLD_FORMATTED_FORMAT      = new ConfigString("infoWorldTimeFormattedFormat", "{\"color\":\"white\",\"text\":\"" +
        "MC time: (day {day}d) {hour}02d:{min}02d:xx\"}", "The format string for the Minecraft time.\nThe supported placeholders are: {DAY_1}, {DAY}, {HOUR}, {MIN}, {SEC}.\n{DAY_1} starts the day counter from 1, {DAY} starts from 0.");

      public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
        BEE_COUNT_FORMAT,
        BIOME_FORMAT,
        BIOME_REG_NAME_FORMAT,
        BLOCK_BREAK_SPEED_FORMAT,
        BLOCK_IN_CHUNK_FORMAT,
        BLOCK_POS_FORMAT,
        BLOCK_PROPS_BOOLEAN_TRUE_FORMAT,
        BLOCK_PROPS_BOOLEAN_FALSE_FORMAT,
        BLOCK_PROPS_DIRECTION_FORMAT,
        BLOCK_PROPS_HEADING_FORMAT,
        BLOCK_PROPS_INT_FORMAT,
        BLOCK_PROPS_SEPARATOR_FORMAT,
        BLOCK_PROPS_STRING_FORMAT,
        CHUNK_POS_FORMAT,
        CHUNK_SECTIONS_FORMAT,
        CHUNK_SECTIONS_FULL_FORMAT,
        CHUNK_UPDATES_FORMAT,
        COORDINATES_FORMAT,
        DIFFICULTY_FORMAT,
        DIMENSION_FORMAT,
        DISTANCE_FORMAT,
        ENTITIES_CLIENT_FORMAT,
        ENTITIES_SERVER_FORMAT,
        ENTITY_REG_NAME_FORMAT,
        FACING_FORMAT,
        FACING_PX_FORMAT,
        FACING_NX_FORMAT,
        FACING_PZ_FORMAT,
        FACING_NZ_FORMAT,
        FPS_FORMAT,
        HONEY_LEVEL_FORMAT,
        LIGHT_LEVEL_CLIENT_FORMAT,
        LIGHT_LEVEL_SERVER_FORMAT,
        LOOKING_AT_BLOCK_FORMAT,
        LOOKING_AT_BLOCK_CHUNK_FORMAT,
        LOOKING_AT_ENTITY_FORMAT,
        LOOKING_AT_ENTITY_LIVING_FORMAT,
        MEMORY_USAGE_FORMAT,
        LOADED_CHUNKS_COUNT_SERVER_FORMAT,
        LOADED_CHUNKS_COUNT_CLIENT_FORMAT,
        PARTICLE_COUNT_FORMAT,
        PING_FORMAT,
        REGION_FILE_FORMAT,
        ROTATION_PITCH_FORMAT,
        ROTATION_YAW_FORMAT,
        SERVER_TPS_VANILLA_FORMAT,
        SERVER_TPS_CARPET_FORMAT,
        SERVER_TPS_NULL_FORMAT,
        SLIME_CHUNK_FORMAT,
        SLIME_CHUNK_YES_FORMAT,
        SLIME_CHUNK_NO_FORMAT,
        SPEED_FORMAT,
        SPEED_AXIS_FORMAT,
        TILE_ENTITIES_FORMAT,
        TIME_DAY_MODULO_FORMAT,
        TIME_REAL_FORMAT,
        TIME_TOTAL_MODULO_FORMAT,
        TIME_WORLD_FORMAT,
        TIME_WORLD_FORMATTED_FORMAT
      );
    }

    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();
                JsonObject objInfoLineOrders = JsonUtils.getNestedObject(root, "InfoLineOrders", false);

                ConfigUtils.readConfigBase(root, "Colors", Configs.Colors.OPTIONS);
                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Formats", Configs.Formats.OPTIONS);
                ConfigUtils.readHotkeyToggleOptions(root, "InfoHotkeys", "InfoTypeToggles", ImmutableList.copyOf(InfoToggle.values()));
                ConfigUtils.readHotkeyToggleOptions(root, "RendererHotkeys", "RendererToggles", ImmutableList.copyOf(RendererToggle.values()));
                ConfigUtils.readConfigBase(root, "StructureColors", StructureToggle.getColorConfigs());
                ConfigUtils.readConfigBase(root, "StructureHotkeys", StructureToggle.getHotkeys());
                ConfigUtils.readConfigBase(root, "StructureToggles", StructureToggle.getToggleConfigs());

                int version = JsonUtils.getIntegerOrDefault(root, "config_version", 0);

                if (objInfoLineOrders != null && version >= 1)
                {
                    for (InfoToggle toggle : InfoToggle.values())
                    {
                        if (JsonUtils.hasInteger(objInfoLineOrders, toggle.getName()))
                        {
                            toggle.setIntegerValue(JsonUtils.getInteger(objInfoLineOrders, toggle.getName()));
                        }
                    }
                }
            }
        }
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();
            JsonObject objInfoLineOrders = JsonUtils.getNestedObject(root, "InfoLineOrders", true);

            ConfigUtils.writeConfigBase(root, "Colors", Configs.Colors.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Formats", Configs.Formats.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "InfoHotkeys", "InfoTypeToggles", ImmutableList.copyOf(InfoToggle.values()));
            ConfigUtils.writeHotkeyToggleOptions(root, "RendererHotkeys", "RendererToggles", ImmutableList.copyOf(RendererToggle.values()));
            ConfigUtils.writeConfigBase(root, "StructureColors", StructureToggle.getColorConfigs());
            ConfigUtils.writeConfigBase(root, "StructureHotkeys", StructureToggle.getHotkeys());
            ConfigUtils.writeConfigBase(root, "StructureToggles", StructureToggle.getToggleConfigs());

            for (InfoToggle toggle : InfoToggle.values())
            {
                objInfoLineOrders.add(toggle.getName(), new JsonPrimitive(toggle.getIntegerValue()));
            }

            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }
}
