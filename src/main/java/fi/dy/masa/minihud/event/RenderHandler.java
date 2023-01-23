package fi.dy.masa.minihud.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;//Maybe changes

import org.graalvm.compiler.code.DataSection;//Maybe changes
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import fi.dy.masa.minihud.config.Configs;
import fi.dy.masa.minihud.config.InfoToggle;
import fi.dy.masa.minihud.config.RendererToggle;
import fi.dy.masa.minihud.mixin.IMixinServerWorld;
import fi.dy.masa.minihud.mixin.IMixinWorldRenderer;
import fi.dy.masa.minihud.renderer.OverlayRenderer;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.minihud.util.IServerEntityManager;
import fi.dy.masa.minihud.util.MiscUtils;

import static java.lang.String.format;

public class RenderHandler implements IRenderer
{
    private static final RenderHandler INSTANCE = new RenderHandler();

    private final MinecraftClient mc;
    private final DataStorage data;
    private final Date date;
    private final Map<ChunkPos, CompletableFuture<WorldChunk>> chunkFutures = new HashMap<>();
    private int fps;
    private int fpsCounter;
    private long fpsUpdateTime = System.currentTimeMillis();
    private long infoUpdateTime;
    private Set<InfoToggle> addedTypes = new HashSet<>();
    @Nullable private WorldChunk cachedClientChunk;

    private final List<StringHolder> lineWrappers = new ArrayList<>();
    private final List<Text> lines = new ArrayList<>();

    public RenderHandler()
    {
        this.mc = MinecraftClient.getInstance();
        this.data = DataStorage.getInstance();
        this.date = new Date();
    }

    public static RenderHandler getInstance()
    {
        return INSTANCE;
    }

    public DataStorage getDataStorage()
    {
        return this.data;
    }

    public static void fixDebugRendererState()
    {
        if (Configs.Generic.FIX_VANILLA_DEBUG_RENDERERS.getBooleanValue())
        {
            //RenderSystem.disableLighting();
            //RenderUtils.color(1, 1, 1, 1);
            //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        }
    }

    private int renderText(int xOff, int yOff, double scale, int textColor, int bgColor, HudAlignment alignment, boolean useBackground, boolean useShadow, List<Text> lines, MatrixStack matrixStack)
    {
      TextRenderer fontRenderer = this.mc.textRenderer;
      final int scaledWidth = GuiUtils.getScaledWindowWidth();
      final int lineHeight = fontRenderer.fontHeight + 2;
      final int contentHeight = lines.size() * lineHeight - 2;
      final int bgMargin = 2;
      if (scale == 0d)
      {
        return 0;
      }
      if (scale != 1d)
      {
        if (scale != 0)
        {
          xOff = (int) (xOff * scale);
          yOff = (int) (yOff * scale);
        }
        RenderSystem.pushMatrix();
        RenderSystem.scaled(scale, scale, 0);
      }
      double posX = xOff + bgMargin;
      double posY = yOff + bgMargin;
      posY = RenderUtils.getHudPosY((int) posY, yOff, contentHeight, scale, alignment);
      posY += RenderUtils.getHudOffsetForPotions(alignment, scale, this.mc.player);
      for(Text line : lines)
      {
        if(line == null) line = Text.of("NULL");
        final int width = fontRenderer.getWidth(line);
        switch (alignment)
        {
          case TOP_RIGHT:
          case BOTTOM_RIGHT:
            posX = (scaledWidth / scale) - width - xOff - bgMargin;
            break;
          case CENTER:
            posX = (scaledWidth / scale / 2) - (width / 2) - xOff;
            break;
          default:
        }
        final int x = (int) posX;
        final int y = (int) posY;
        posY += lineHeight;
        if (useBackground)
        {
          RenderUtils.drawRect(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.fontHeight, bgColor);
        }
        if (useShadow)
        {
          fontRenderer.drawWithShadow(matrixStack, line, x, y, textColor);
        }
        else
        {
          fontRenderer.draw(matrixStack, line, x, y, textColor);
        }
      }
      if (scale != 1d)
      {
        RenderSystem.popMatrix();
      }
      return contentHeight + bgMargin * 2;
    }

    @Override
    public void onRenderGameOverlayPost(MatrixStack matrixStack)
    {
        if (Configs.Generic.ENABLED.getBooleanValue() == false)
        {
            this.resetCachedChunks();
            return;
        }

        if (Configs.Generic.ENABLED.getBooleanValue() &&
            this.mc.options.debugEnabled == false &&
            this.mc.player != null && this.mc.options.hudHidden == false &&
            (Configs.Generic.REQUIRE_SNEAK.getBooleanValue() == false || this.mc.player.isSneaking()) &&
            Configs.Generic.REQUIRED_KEY.getKeybind().isKeybindHeld())
        {
            if (InfoToggle.FPS.getBooleanValue())
            {
                this.updateFps();
            }

            long currentTime = System.currentTimeMillis();

            // Only update the text once per game tick
            if (currentTime - this.infoUpdateTime >= 50)
            {
                this.updateLines();
                this.infoUpdateTime = currentTime;
            }

            int x = Configs.Generic.TEXT_POS_X.getIntegerValue();
            int y = Configs.Generic.TEXT_POS_Y.getIntegerValue();
            int textColor = Configs.Colors.TEXT_COLOR.getIntegerValue();
            int bgColor = Configs.Colors.TEXT_BACKGROUND_COLOR.getIntegerValue();
            HudAlignment alignment = (HudAlignment) Configs.Generic.HUD_ALIGNMENT.getOptionListValue();
            boolean useBackground = Configs.Generic.USE_TEXT_BACKGROUND.getBooleanValue();
            boolean useShadow = Configs.Generic.USE_FONT_SHADOW.getBooleanValue();

            renderText(x, y, Configs.Generic.FONT_SCALE.getDoubleValue(), textColor, bgColor, alignment, useBackground, useShadow, this.lines, matrixStack);
        }
    }

    @Override
    public void onRenderTooltipLast(ItemStack stack, int x, int y)
    {
        if (stack.getItem() instanceof FilledMapItem)
        {
            if (Configs.Generic.MAP_PREVIEW.getBooleanValue())
            {
                fi.dy.masa.malilib.render.RenderUtils.renderMapPreview(stack, x, y, Configs.Generic.MAP_PREVIEW_SIZE.getIntegerValue());
            }
        }
        else if (Configs.Generic.SHULKER_BOX_PREVIEW.getBooleanValue())
        {
            boolean render = Configs.Generic.SHULKER_DISPLAY_REQUIRE_SHIFT.getBooleanValue() == false || GuiBase.isShiftDown();

            if (render)
            {
                fi.dy.masa.malilib.render.RenderUtils.renderShulkerBoxPreview(stack, x, y, Configs.Generic.SHULKER_DISPLAY_BACKGROUND_COLOR.getBooleanValue());
            }
        }
    }

    @Override
    public void onRenderWorldLast(MatrixStack matrixStack, Matrix4f projMatrix)
    {
        if (Configs.Generic.ENABLED.getBooleanValue() &&
            this.mc.world != null && this.mc.player != null && this.mc.options.hudHidden == false)
        {
            OverlayRenderer.renderOverlays(matrixStack, projMatrix, this.mc);
        }
    }

    public int getSubtitleOffset()
    {
        HudAlignment align = (HudAlignment) Configs.Generic.HUD_ALIGNMENT.getOptionListValue();

        if (align == HudAlignment.BOTTOM_RIGHT)
        {
            int offset = (int) (this.lineWrappers.size() * (StringUtils.getFontHeight() + 2) * Configs.Generic.FONT_SCALE.getDoubleValue());

            return -(offset - 16);
        }

        return 0;
    }

    private void updateFps()
    {
        this.fpsCounter++;
        long time = System.currentTimeMillis();

        if (time >= (this.fpsUpdateTime + 1000L))
        {
            this.fpsUpdateTime = time;
            this.fps = this.fpsCounter;
            this.fpsCounter = 0;
        }
    }

    public void updateData(MinecraftClient mc)
    {
        if (mc.world != null)
        {
            if (RendererToggle.OVERLAY_STRUCTURE_MAIN_TOGGLE.getBooleanValue())
            {
                DataStorage.getInstance().updateStructureData();
            }
        }
    }

    private void updateLines()
    {
        this.lineWrappers.clear();
        this.addedTypes.clear();

        if (this.chunkFutures.size() >= 4)
        {
            this.resetCachedChunks();
        }

        // Get the info line order based on the configs
        List<LinePos> positions = new ArrayList<LinePos>();

        for (InfoToggle toggle : InfoToggle.values())
        {
            if (toggle.getBooleanValue())
            {
                positions.add(new LinePos(toggle.getIntegerValue(), toggle));
            }
        }

        Collections.sort(positions);

        for (LinePos pos : positions)
        {
            try
            {
                this.addLine(pos.type);
            }
            catch (Exception e)
            {
                this.addLine(pos.type.getName() + ": exception");
            }
        }

        if (Configs.Generic.SORT_LINES_BY_LENGTH.getBooleanValue())
        {
            Collections.sort(this.lineWrappers);

            if (Configs.Generic.SORT_LINES_REVERSED.getBooleanValue())
            {
                Collections.reverse(this.lineWrappers);
            }
        }

        this.lines.clear();

        for (StringHolder holder : this.lineWrappers)
        {
          try
          {
            this.lines.add(Text.Serializer.fromLenientJson(holder.str));
          }
          catch(Exception e)
          {
            this.lines.add(Text.of("Formatting failed - Invalid JSON"));
          }
        }
    }

    private void addLine(String text)
    {
      this.lineWrappers.add(new StringHolder(text));
    }

    private String applyColors(String str)
    {
      return str.replace("{colorfg}", Configs.Colors.COLORFG.getStringValue())
        .replace("{colorbg}", Configs.Colors.COLORBG.getStringValue())
        .replace("{color0}", Configs.Colors.COLOR0.getStringValue())
        .replace("{color1}", Configs.Colors.COLOR1.getStringValue())
        .replace("{color2}", Configs.Colors.COLOR2.getStringValue())
        .replace("{color3}", Configs.Colors.COLOR3.getStringValue())
        .replace("{color4}", Configs.Colors.COLOR4.getStringValue())
        .replace("{color5}", Configs.Colors.COLOR5.getStringValue())
        .replace("{color6}", Configs.Colors.COLOR6.getStringValue())
        .replace("{color7}", Configs.Colors.COLOR7.getStringValue())
        .replace("{color8}", Configs.Colors.COLOR8.getStringValue())
        .replace("{color9}", Configs.Colors.COLOR9.getStringValue())
        .replace("{color10}", Configs.Colors.COLOR10.getStringValue())
        .replace("{color11}", Configs.Colors.COLOR11.getStringValue())
        .replace("{color12}", Configs.Colors.COLOR12.getStringValue())
        .replace("{color13}", Configs.Colors.COLOR13.getStringValue())
        .replace("{color14}", Configs.Colors.COLOR14.getStringValue())
        .replace("{color15}", Configs.Colors.COLOR15.getStringValue());
    }

    private void addLine(InfoToggle type)
    {
        MinecraftClient mc = this.mc;
        Entity entity = mc.getCameraEntity();
        World world = entity.getEntityWorld();
        double y = entity.getY();
        BlockPos pos = new BlockPos(entity.getX(), y, entity.getZ());
        ChunkPos chunkPos = new ChunkPos(pos);

        @SuppressWarnings("deprecation")
        boolean isChunkLoaded = mc.world.isChunkLoaded(pos);
        if (isChunkLoaded == false) return;

        switch(type)
        {
          case FPS:
            this.addLine(format(applyColors(Configs.Formats.FPS_FORMAT.getStringValue()).replace("{FPS}", "%"), this.fps));
            break;

          case MEMORY_USAGE:
            long memMax = Runtime.getRuntime().maxMemory();
            long memTotal = Runtime.getRuntime().totalMemory();
            long memFree = Runtime.getRuntime().freeMemory();
            long memUsed = memTotal - memFree;

            this.addLine(format(format(format(format(format(applyColors(Configs.Formats.MEMORY_USAGE_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{pused}", "%"), memUsed * 100L / memMax)
                .replace("{used}", "%"), MiscUtils.bytesToMb(memUsed))
                .replace("{max}", "%"), MiscUtils.bytesToMb(memMax))
                .replace("{pallocated}", "%"), memTotal * 100L / memMax)
                .replace("{total}", "%"), MiscUtils.bytesToMb(memTotal)).replace("\0", "%"));
            break;

          case TIME_REAL:
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("G:GGGG:y:yy:yyy:yyyy:Y:YY:YYY:YYYY:M:MM:MMM:w:ww:W:D:DD:DDD:d:dd:F:E:EEEE:u:a:aaaa:H:HH:k:kk:K:KK:h:hh:m:mm:s:ss:S:SS:SSS:z:zzzz:Z:X");
                this.date.setTime(System.currentTimeMillis());
                String[] times = sdf.format(this.date).split(":");
                //this.addLine(sdf.format(this.date));
                this.addLine(applyColors(Configs.Formats.TIME_REAL_FORMAT.getStringValue())
                  .replace("{G}", times[0]).replace("{GGGG}", times[1])
                  .replace("{y}", times[2]).replace("{yy}", times[3]).replace("{yyy}", times[4]).replace("{yyyy}", times[5])
                  .replace("{Y}", times[6]).replace("{YY}", times[7]).replace("{YYY}", times[8]).replace("{YYYY}", times[9])
                  .replace("{M}", times[10]).replace("{MM}", times[11]).replace("{MMM}", times[12])
                  .replace("{w}", times[13]).replace("{ww}", times[14])
                  .replace("{W}", times[15])
                  .replace("{D}", times[16]).replace("{DD}", times[17]).replace("{DDD}", times[18])
                  .replace("{d}", times[19]).replace("{dd}", times[20])
                  .replace("{F}", times[21])
                  .replace("{E}", times[22]).replace("{EEEE}", times[23])
                  .replace("{u}", times[24])
                  .replace("{a}", times[25]).replace("{aaaa}", times[26])
                  .replace("{H}", times[27]).replace("{HH}", times[28])
                  .replace("{k}", times[29]).replace("{kk}", times[30])
                  .replace("{K}", times[31]).replace("{KK}", times[32])
                  .replace("{h}", times[33]).replace("{hh}", times[34])
                  .replace("{m}", times[35]).replace("{mm}", times[36])
                  .replace("{s}", times[37]).replace("{ss}", times[38])
                  .replace("{S}", times[39]).replace("{SS}", times[40]).replace("{SSS}", times[41])
                  .replace("{z}", times[42]).replace("{zzzz}", times[43])
                  .replace("{Z}", times[44])
                  .replace("{X}", times[45]));
            }
            catch (Exception e)
            {
                this.addLine("\"Date formatting failed - Invalid date format string?\"");
            }
            break;

          case TIME_WORLD:

            this.addLine(format(format(applyColors(Configs.Formats.TIME_WORLD_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{time}", "%"), world.getTimeOfDay())
                .replace("{total}", "%"), world.getTime()).replace("\0", "%"));
            break;

          case TIME_WORLD_FORMATTED:
            try
            {
                long timeDay = world.getTimeOfDay();
                long day = (int) (timeDay / 24000);
                // 1 tick = 3.6 seconds in MC (0.2777... seconds IRL)
                int dayTicks = (int) (timeDay % 24000);
                int hour = (int) ((dayTicks / 1000) + 6) % 24;
                int min = (int) (dayTicks / 16.666666) % 60;
                int sec = (int) (dayTicks / 0.277777) % 60;

            this.addLine(format(format(format(format(format(applyColors(Configs.Formats.TIME_WORLD_FORMATTED_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{day}", "%"), day)
                .replace("{day_1}", "%"), day + 1)
                .replace("{hour}", "%"), hour)
                .replace("{min}", "%"), min)
                .replace("{sec}", "%"), sec).replace("\0", "%"));
            }
            catch (Exception e)
            {
                this.addLine("\"Date formatting failed - Invalid date format string?\"");
            }
            break;

          case TIME_DAY_MODULO:
            int mod = Configs.Generic.TIME_DAY_DIVISOR.getIntegerValue();
            this.addLine(format(format(applyColors(Configs.Formats.TIME_DAY_MODULO_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{mod}", "%"), mod)
              .replace("{time}", "%"), world.getTimeOfDay() % mod).replace("\0", "%"));
            break;

          case TIME_TOTAL_MODULO:
            int mod1 = Configs.Generic.TIME_TOTAL_DIVISOR.getIntegerValue();
            this.addLine(format(format(applyColors(Configs.Formats.TIME_TOTAL_MODULO_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{mod}", "%"), mod1)
              .replace("{time}", "%"), world.getTime() % mod1).replace("\0", "%"));
            break;

          case SERVER_TPS:
            if (mc.isIntegratedServerRunning() && (mc.getServer().getTicks() % 10) == 0)
            {
                this.data.updateIntegratedServerTPS();
            }

            if (this.data.hasTPSData())
            {
              double tps = this.data.getServerTPS();
              double mspt = this.data.getServerMSPT();
              String rst = GuiBase.TXT_RST;
              String preTps = tps >= 20.0D ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
              String preMspt;

              // Carpet server and integrated server have actual meaningful MSPT data available
              if (this.data.isCarpetServer() || mc.isInSingleplayer())
              {
                if      (mspt <= 40) { preMspt = GuiBase.TXT_GREEN; }
                else if (mspt <= 45) { preMspt = GuiBase.TXT_YELLOW; }
                else if (mspt <= 50) { preMspt = GuiBase.TXT_GOLD; }
                else                 { preMspt = GuiBase.TXT_RED; }

                this.addLine(format(format(applyColors(Configs.Formats.SERVER_TPS_CARPET_FORMAT.getStringValue())
                  .replace("%", "\0")
                  .replace("{preTps}", preTps)
                  .replace("{tps}", "%"), tps)
                  .replace("{rst}", rst)
                  .replace("{preMspt}", preMspt)
                  .replace("{mspt}", "%"), mspt).replace("\0", "%"));
              }
              else
              {
                if (mspt <= 51) { preMspt = GuiBase.TXT_GREEN; }
                else            { preMspt = GuiBase.TXT_RED; }

                this.addLine(format(format(applyColors(Configs.Formats.SERVER_TPS_VANILLA_FORMAT.getStringValue())
                  .replace("%", "\0")
                  .replace("{preTps}", preTps)
                  .replace("{tps}", "%"), tps)
                  .replace("{rst}", rst)
                  .replace("{preMspt}", preMspt)
                  .replace("{mspt}", "%"), mspt).replace("\0", "%"));
              }
            }
            else
            {
              this.addLine(applyColors(Configs.Formats.SERVER_TPS_NULL_FORMAT.getStringValue()));
            }
            break;

          case PING:
            PlayerListEntry info = mc.player.networkHandler.getPlayerListEntry(mc.player.getUuid());

            if (info != null)
            {
              this.addLine(format(applyColors(Configs.Formats.PING_FORMAT.getStringValue()).replace("{ping}", "%"), info.getLatency()));
            }
            break;

          case COORDINATES:
          //ADD case COORDINATES_SCALED:
          case DIMENSION:
            // Don't add the same line multiple times
            if (this.addedTypes.contains(InfoToggle.COORDINATES) || this.addedTypes.contains(InfoToggle.DIMENSION))
              return;
            //String pre = "";
            StringBuilder str = new StringBuilder(128);
            str.append("[");
            if (InfoToggle.COORDINATES.getBooleanValue())
            {
              str.append(format(format(format(applyColors(Configs.Formats.COORDINATES_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{x}", "%"), entity.getX())
                .replace("{y}", "%"), y)
                .replace("{z}", "%"), entity.getZ()).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.COORDINATES);
            }
            if (InfoToggle.DIMENSION.getBooleanValue())
            {
              if(InfoToggle.COORDINATES.getBooleanValue()) str.append(",");
              str.append(applyColors(Configs.Formats.DIMENSION_FORMAT.getStringValue())
                .replace("{dim}", world.getRegistryKey().getValue().toString()));
              this.addedTypes.add(InfoToggle.DIMENSION);
            }
            this.addLine(str.append("]").toString());
            break;

          case BLOCK_POS:
          case CHUNK_POS:
          case REGION_FILE:
            // Don't add the same line multiple times
            if (this.addedTypes.contains(InfoToggle.BLOCK_POS) ||
                this.addedTypes.contains(InfoToggle.CHUNK_POS) ||
                this.addedTypes.contains(InfoToggle.REGION_FILE))
              return;

            String pre1 = "";
            StringBuilder str1 = new StringBuilder(256);
            str1.append("[");
            Boolean hasOther = InfoToggle.BLOCK_POS.getBooleanValue();
            if (hasOther)
            {
              str1.append(format(format(format(applyColors(Configs.Formats.BLOCK_POS_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{x}", "%"), pos.getX())
                .replace("{y}", "%"), pos.getY())
                .replace("{z}", "%"), pos.getZ()).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.BLOCK_POS);
            }
            if (InfoToggle.CHUNK_POS.getBooleanValue())
            {
              if(hasOther) str1.append(",");
              str1.append(format(format(format(applyColors(Configs.Formats.CHUNK_POS_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{x}", "%"), chunkPos.x)
                .replace("{y}", "%"), pos.getY() >> 4)
                .replace("{z}", "%"), chunkPos.z).replace("\0", "%"));
              hasOther = true;
              this.addedTypes.add(InfoToggle.CHUNK_POS);
            }
            if (InfoToggle.REGION_FILE.getBooleanValue())
            {
              if(hasOther) str1.append(",");
              str1.append(format(format(applyColors(Configs.Formats.REGION_FILE_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{x}", "%"), pos.getX() >> 9)
                .replace("{z}", "%"), pos.getZ() >> 9).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.REGION_FILE);
            }
            this.addLine(str1.append("]").toString());
            break;

          case BLOCK_IN_CHUNK:
            this.addLine(format(format(format(format(format(format(applyColors(Configs.Formats.BLOCK_IN_CHUNK_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{x}", "%"), pos.getX() & 0xF)
              .replace("{y}", "%"), pos.getY() & 0xF)
              .replace("{z}", "%"), pos.getZ() & 0xf)
              .replace("{cx}", "%"), chunkPos.x)
              .replace("{cy}", "%"), pos.getY() >> 4)
              .replace("{cz}", "%"), chunkPos.z).replace("\0", "%"));
            break;

          case BLOCK_BREAK_SPEED:
            this.addLine(format(applyColors(Configs.Formats.BLOCK_BREAK_SPEED_FORMAT.getStringValue())
              .replace("%", "\0").replace("{bbs}", "%"), DataStorage.getInstance().getBlockBreakingSpeed()).replace("\0", "%"));
            break;

          case DISTANCE:
            Vec3d ref = DataStorage.getInstance().getDistanceReferencePoint();
            this.addLine(format(format(format(format(format(format(format(applyColors(Configs.Formats.DISTANCE_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{d}", "%"), Math.sqrt(ref.squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ())))
              .replace("{dx}", "%"), entity.getX() - ref.x)
              .replace("{dy}", "%"), entity.getY() - ref.y)
              .replace("{dz}", "%"), entity.getZ() - ref.z)
              .replace("{rx}", "%"), ref.x)
              .replace("{ry}", "%"), ref.y)
              .replace("{rz}", "%"), ref.z).replace("\0", "%"));
            break;

          case FACING:
            Direction facing = entity.getHorizontalFacing();
            String str2 = "Invalid";

            switch (facing)
            {
              case NORTH: str2 = applyColors(Configs.Formats.FACING_NZ_FORMAT.getStringValue()); break;
              case SOUTH: str2 = applyColors(Configs.Formats.FACING_PZ_FORMAT.getStringValue()); break;
              case WEST: str2 = applyColors(Configs.Formats.FACING_NX_FORMAT.getStringValue()); break;
              case EAST: str2 = applyColors(Configs.Formats.FACING_PX_FORMAT.getStringValue()); break;
              default:
            }

            this.addLine(applyColors(Configs.Formats.FACING_FORMAT.getStringValue()).replace("{dir}", facing.toString()).replace("{coord}", str2));
            break;

          case LIGHT_LEVEL_CLIENT:
          case LIGHT_LEVEL_SERVER:
            if(this.addedTypes.contains(InfoToggle.LIGHT_LEVEL_CLIENT) || this.addedTypes.contains(InfoToggle.LIGHT_LEVEL_SERVER))
              return;
            WorldChunk clientChunk = this.getClientChunk(chunkPos);
            if (clientChunk.isEmpty() == false)
            {
              if(InfoToggle.LIGHT_LEVEL_CLIENT.getBooleanValue())
              {
                LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
                this.addLine(format(format(format(applyColors(Configs.Formats.LIGHT_LEVEL_CLIENT_FORMAT.getStringValue())
                  .replace("%", "\0")
                  .replace("{light}", "%"), lightingProvider.getLight(pos, 0))
                  .replace("{block}", "%"), lightingProvider.get(LightType.BLOCK).getLightLevel(pos))
                  .replace("{sky}", "%"), lightingProvider.get(LightType.SKY).getLightLevel(pos)).replace("\0", "%"));
                this.addedTypes.add(InfoToggle.LIGHT_LEVEL_CLIENT);
              }
              if(InfoToggle.LIGHT_LEVEL_SERVER.getBooleanValue())
              {
                World bestWorld = WorldUtils.getBestWorld(mc);
                WorldChunk serverChunk = this.getChunk(chunkPos);
                if (serverChunk != null && serverChunk != clientChunk)
                {
                  LightingProvider lightingProvider = bestWorld.getChunkManager().getLightingProvider();
                  this.addLine(format(format(format(applyColors(Configs.Formats.LIGHT_LEVEL_SERVER_FORMAT.getStringValue())
                    .replace("%", "\0")
                    .replace("{light}", "%"), lightingProvider.getLight(pos, 0))
                    .replace("{block}", "%"), lightingProvider.get(LightType.BLOCK).getLightLevel(pos))
                    .replace("{sky}", "%"), lightingProvider.get(LightType.SKY).getLightLevel(pos)).replace("\0", "%"));
                  this.addedTypes.add(InfoToggle.LIGHT_LEVEL_SERVER);
                }
              }
            }
            break;

          case BEE_COUNT:
            World bestWorld = WorldUtils.getBestWorld(mc);
            BlockEntity be = this.getTargetedBlockEntity(bestWorld, mc);
            if (be instanceof BeehiveBlockEntity)
              this.addLine(applyColors(Configs.Formats.BEE_COUNT_FORMAT.getStringValue())
                .replace("{bees}", Integer.toString(((BeehiveBlockEntity) be).getBeeCount())));
            break;
            
            //ADD FURNACE XP

          case HONEY_LEVEL:
            BlockState state = this.getTargetedBlock(mc);

            if (state != null && state.getBlock() instanceof BeehiveBlock)
            {
              this.addLine(applyColors(Configs.Formats.HONEY_LEVEL_FORMAT.getStringValue())
                .replace("{honey}", Integer.toString(BeehiveBlockEntity.getHoneyLevel(state))));
            }
            break;
//ADD HONSE
          case ROTATION_YAW:
          case ROTATION_PITCH:
          case SPEED:
            // Don't add the same line multiple times
            if (this.addedTypes.contains(InfoToggle.ROTATION_YAW) ||
                this.addedTypes.contains(InfoToggle.ROTATION_PITCH) ||
                this.addedTypes.contains(InfoToggle.SPEED))
              return;
            StringBuilder str3 = new StringBuilder(128);
            str3.append("[");
            Boolean hasOther1 = InfoToggle.ROTATION_YAW.getBooleanValue();
            if(hasOther1)
            {
              str3.append(format(applyColors(Configs.Formats.ROTATION_YAW_FORMAT.getStringValue())
                .replace("%", "\0").replace("{yaw}", "%"), MathHelper.wrapDegrees(entity.getYaw())).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.ROTATION_YAW);
            }

            if (InfoToggle.ROTATION_PITCH.getBooleanValue())
            {
              if(hasOther1) str3.append(",");
              str3.append(format(applyColors(Configs.Formats.ROTATION_PITCH_FORMAT.getStringValue())
                .replace("%", "\0").replace("{pitch}", "%"), MathHelper.wrapDegrees(entity.getPitch())).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.ROTATION_PITCH);
              hasOther1 = true;
            }

            if (InfoToggle.SPEED.getBooleanValue())
            {
              if(hasOther1) str3.append(",");
              double dx = entity.getX() - entity.lastRenderX;
              double dy = entity.getY() - entity.lastRenderY;
              double dz = entity.getZ() - entity.lastRenderZ;
              double dist1 = Math.sqrt(dx * dx + dy * dy + dz * dz);
              str3.append(format(applyColors(Configs.Formats.SPEED_FORMAT.getStringValue())
                .replace("%", "\0").replace("{speed}", "%"), dist1 * 20).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.SPEED);
            }
            this.addLine(str3.append("]").toString());
            break;

// ADD SPEED HV
          case SPEED_AXIS:
            this.addLine(format(format(format(applyColors(Configs.Formats.SPEED_AXIS_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{x}", "%"), (entity.getX() - entity.lastRenderX) * 20)
              .replace("{y}", "%"), (entity.getY() - entity.lastRenderY) * 20)
              .replace("{z}", "%"), (entity.getZ() - entity.lastRenderZ) * 20).replace("\0", "%"));
            break;

          case CHUNK_SECTIONS:
            this.addLine(format(applyColors(Configs.Formats.CHUNK_SECTIONS_FORMAT.getStringValue())
              .replace("%", "\0").replace("{c}", "%"), ((IMixinWorldRenderer) mc.worldRenderer).getRenderedChunksInvoker()).replace("\0", "%"));
            break;

          case CHUNK_SECTIONS_FULL:
            this.addLine(applyColors(Configs.Formats.CHUNK_SECTIONS_FULL_FORMAT.getStringValue())
              .replace("{c}", mc.worldRenderer.getChunksDebugString()));
            break;

          case CHUNK_UPDATES:
            this.addLine("TODO" /*format("Chunk updates: %d", ChunkRenderer.chunkUpdateCount)*/);
            break;

          case LOADED_CHUNKS_COUNT:
            String chunksClient = mc.world.asString();
            World worldServer = WorldUtils.getBestWorld(mc);

            if (worldServer != null && worldServer != mc.world)
            {
                this.addLine(format(format(applyColors(Configs.Formats.LOADED_CHUNKS_COUNT_SERVER_FORMAT.getStringValue())
                  .replace("%", "\0")
                  .replace("{chunks}", "%"), ((ServerChunkManager) worldServer.getChunkManager()).getLoadedChunkCount())
                  .replace("{total}", "%"), ((ServerChunkManager) worldServer.getChunkManager()).getTotalChunksLoadedCount())
                  .replace("{client}", chunksClient).replace("\0", "%"));
            }
            else
            {
                this.addLine(applyColors(Configs.Formats.LOADED_CHUNKS_COUNT_CLIENT_FORMAT.getStringValue()).replace("{client}", chunksClient));
            }
            break;

          case PARTICLE_COUNT:
            this.addLine(applyColors(Configs.Formats.PARTICLE_COUNT_FORMAT.getStringValue())
              .replace("{p}", mc.particleManager.getDebugString()));
            break;

          case DIFFICULTY:
            long chunkInhabitedTime = 0L;
            float moonPhaseFactor = 0.0F;
            WorldChunk serverChunk = this.getChunk(chunkPos);

            if (serverChunk != null)
            {
                moonPhaseFactor = mc.world.getMoonSize();
                chunkInhabitedTime = serverChunk.getInhabitedTime();
            }
            LocalDifficulty diff = new LocalDifficulty(mc.world.getDifficulty(), mc.world.getTimeOfDay(), chunkInhabitedTime, moonPhaseFactor);
            this.addLine(format(format(format(applyColors(Configs.Formats.DIFFICULTY_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{local}", "%"), diff.getLocalDifficulty())
              .replace("{clamped}", "%"), diff.getClampedLocalDifficulty())
              .replace("{day}", "%"), mc.world.getTimeOfDay() / 24000L).replace("\0", "%"));
            break;

          case BIOME:
            WorldChunk clientChunk = this.getClientChunk(chunkPos);

            if (clientChunk.isEmpty() == false)
            {
                Biome biome = mc.world.getBiome(pos);
                Identifier id = mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                this.addLine(applyColors(Configs.Formats.BIOME_FORMAT.getStringValue())
                  .replace("{biome}", StringUtils.translate("biome." + id.toString().replace(":", "."))));
            }
            break;

          case BIOME_REG_NAME:
            WorldChunk clientChunk = this.getClientChunk(chunkPos);

            if (clientChunk.isEmpty() == false)
            {
                Biome biome = mc.world.getBiome(pos);
                Identifier rl = mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                this.addLine(applyColors(Configs.Formats.BIOME_REG_NAME_FORMAT.getStringValue())
                  .replace("{name}", rl != null ? rl.toString() : "?"));
            }
            break;

          case TILE_ENTITIES:
            // TODO 1.17
            //this.addLine(String.format("Client world TE - L: %d, T: %d", mc.world.blockEntities.size(), mc.world.tickingBlockEntities.size()));
            this.addLine(format(format(applyColors(Configs.Formats.TILE_ENTITIES_FORMAT.getStringValue())
              .replace("%", "\0")
              .replace("{loaded}", "%"), "? (TODO 1.17)")
              .replace("{ticking}", "%"), "? (TODO 1.17)").replace("\0", "%"));
            break;

          case ENTITIES_CLIENT:
          case ENTITIES_SERVER:
            if (this.addedTypes.contains(InfoToggle.ENTITIES_CLIENT) ||
                this.addedTypes.contains(InfoToggle.ENTITIES_SERVER))
              return;
            StringBuilder str4 = new StringBuilder(128);
            str4.append("[");
            Boolean hasOther2 = InfoToggle.ENTITIES_CLIENT.getBooleanValue();
            if(hasOther2)
            {
              str4.append(format(applyColors(Configs.Formats.ENTITIES_CLIENT_FORMAT.getStringValue())
                .replace("%", "\0")
                .replace("{e}", "%"), mc.world.getRegularEntityCount()).replace("\0", "%"));
              this.addedTypes.add(InfoToggle.ENTITIES_CLIENT);
            }
            if(InfoToggle.ENTITIES_SERVER.getBooleanValue() && mc.isIntegratedServerRunning())
            {
                World serverWorld = WorldUtils.getBestWorld(mc);
                if (serverWorld instanceof ServerWorld)
                {
                  if(hasOther2) str4.append(",");
                  str4.append(format(applyColors(Configs.Formats.ENTITIES_SERVER_FORMAT.getStringValue())
                    .replace("%", "\0")
                    .replace("{e}", "%"), ((IServerEntityManager) ((IMixinServerWorld) serverWorld).minihud_getEntityManager()).getIndexSize()).replace("\0", "%"));
                  this.addedTypes.add(InfoToggle.ENTITIES_SERVER);
                  hasOther2 = true;
                }
            }
            if(hasOther2) this.addLine(str4.append("]").toString());
            break;

          case SLIME_CHUNK:
            if (MiscUtils.isOverworld(world) == false)
              return;
            if (this.data.isWorldSeedKnown(world))
            {
                long seed = this.data.getWorldSeed(world);
                if (MiscUtils.canSlimeSpawnAt(pos.getX(), pos.getZ(), seed))
                {
                  this.addLine(applyColors(Configs.Formats.SLIME_CHUNK_FORMAT.getStringValue())
                    .replace("{result}", applyColors(Configs.Formats.SLIME_CHUNK_YES_FORMAT.getStringValue())));
                }
                else
                {
                  this.addLine(applyColors(Configs.Formats.SLIME_CHUNK_FORMAT.getStringValue())
                    .replace("{result}", applyColors(Configs.Formats.SLIME_CHUNK_NO_FORMAT.getStringValue())));
                }
            }
            else
            {
              this.addLine(applyColors(Configs.Formats.SLIME_CHUNK_NO_SEED_FORMAT.getStringValue()));
            }
            break;

          case LOOKING_AT_ENTITY:
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY)
            {
              Entity lookedEntity = ((EntityHitResult) mc.crosshairTarget).getEntity();
              if (lookedEntity instanceof LivingEntity)
              {
                LivingEntity living = (LivingEntity) lookedEntity;
                this.addLine(format(format(applyColors(Configs.Formats.LOOKING_AT_ENTITY_LIVING_FORMAT.getStringValue())
                  .replace("%", "\0")
                  .replace("{entity}", lookedEntity.getName().getString())
                  .replace("{hp}", "%"), living.getHealth())
                  .replace("{maxhp}", "%"), living.getMaxHealth()).replace("\0", "%"));
              }
              else
              {
                this.addLine(applyColors(Configs.Formats.LOOKING_AT_ENTITY_FORMAT.getStringValue())
                  .replace("{entity}", lookedEntity.getName().getString()));
              }
            }
            break;

          case ENTITY_REG_NAME:
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY)
            {
              Entity lookedEntity = ((EntityHitResult) mc.crosshairTarget).getEntity();
              Identifier regName = EntityType.getId(lookedEntity.getType());

              if (regName != null)
              {
                this.addLine(applyColors(Configs.Formats.ENTITY_REG_NAME_FORMAT.getStringValue())
                  .replace("{name}", regName.toString()));
              }
            }
            break;

          case LOOKING_AT_BLOCK:
          case LOOKING_AT_BLOCK_CHUNK:
            // Don't add the same line multiple times
            if (this.addedTypes.contains(InfoToggle.LOOKING_AT_BLOCK) ||
                this.addedTypes.contains(InfoToggle.LOOKING_AT_BLOCK_CHUNK))
              return;
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
            {
                BlockPos lookPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
                StringBuilder str5 = new StringBuilder(128);
                str5.append("[");
                Boolean hasOther3 = InfoToggle.LOOKING_AT_BLOCK.getBooleanValue();
                if(hasOther3)
                {
                  str5.append(format(format(format(applyColors(Configs.Formats.LOOKING_AT_BLOCK_FORMAT.getStringValue())
                    .replace("%", "\0")
                    .replace("{x}", "%"), lookPos.getX())
                    .replace("{y}", "%"), lookPos.getY())
                    .replace("{z}", "%"), lookPos.getZ()).replace("\0", "%"));
                  this.addedTypes.add(InfoToggle.LOOKING_AT_BLOCK);
                }

                if (InfoToggle.LOOKING_AT_BLOCK_CHUNK.getBooleanValue())
                {
                  if(hasOther3) str5.append(",");
                  str5.append(format(format(format(format(format(format(applyColors(Configs.Formats.LOOKING_AT_BLOCK_CHUNK_FORMAT.getStringValue())
                    .replace("%", "\0")
                    .replace("{x}", "%"), lookPos.getX() & 0xf)
                    .replace("{y}", "%"), lookPos.getY() & 0xf)
                    .replace("{z}", "%"), lookPos.getZ() & 0xf)
                    .replace("{cx}", "%"), lookPos.getX() >> 4)
                    .replace("{cy}", "%"), lookPos.getY() >> 4)
                    .replace("{cz}", "%"), lookPos.getZ() >> 4).replace("\0", "%"));
                  this.addedTypes.add(InfoToggle.LOOKING_AT_BLOCK_CHUNK);
                }
                str5.append("]");
                this.addLine(str5.toString());
            }
            break;

          case BLOCK_PROPS:
            this.getBlockProperties(mc);
            break;

        }
    }

    @Nullable
    private BlockEntity getTargetedBlockEntity(World world, MinecraftClient mc)
    {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockPos posLooking = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            WorldChunk chunk = this.getChunk(new ChunkPos(posLooking));

            // The method in World now checks that the caller is from the same thread...
            return chunk != null ? chunk.getBlockEntity(posLooking) : null;
        }

        return null;
    }

    @Nullable
    private BlockState getTargetedBlock(MinecraftClient mc)
    {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockPos posLooking = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            return mc.world.getBlockState(posLooking);
        }

        return null;
    }

    private <T extends Comparable<T>> void getBlockProperties(MinecraftClient mc)
    {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockPos posLooking = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            BlockState state = mc.world.getBlockState(posLooking);
            Identifier rl = Registry.BLOCK.getId(state.getBlock());

            this.addLine(applyColors(Configs.Formats.BLOCK_PROPS_HEADING_FORMAT.getStringValue()).replace("{name}", rl != null ? rl.toString() : "<null>"));
            String separator = applyColors(Configs.Formats.BLOCK_PROPS_SEPARATOR_FORMAT.getStringValue());

            Collection<Property<?>> properties = state.getProperties();
            if (properties.size() > 0)
              for (Property<?> prop : properties)
              {
                Comparable<?> val = state.get(prop);
                if (prop instanceof BooleanProperty)
                {
                  this.addLine(val.equals(Boolean.TRUE) ?
                    applyColors(Configs.Formats.BLOCK_PROPS_BOOLEAN_TRUE_FORMAT.getStringValue()).replace("{prop}", prop.getName()).replace("{separator}", separator):
                    applyColors(Configs.Formats.BLOCK_PROPS_BOOLEAN_FALSE_FORMAT.getStringValue()).replace("{prop}", prop.getName()).replace("{separator}", separator));
                }
                else if (prop instanceof DirectionProperty)
                {
                  this.addLine(applyColors(Configs.Formats.BLOCK_PROPS_DIRECTION_FORMAT.getStringValue())
                    .replace("{prop}", prop.getName()).replace("{separator}", separator).replace("{value}", val.toString()));
                }
                else if (prop instanceof IntProperty)
                {
                  this.addLine(applyColors(Configs.Formats.BLOCK_PROPS_INT_FORMAT.getStringValue())
                    .replace("{prop}", prop.getName()).replace("{separator}", separator).replace("{value}", val.toString()));
                }
                else
                {
                  this.addLine(applyColors(Configs.Formats.BLOCK_PROPS_STRING_FORMAT.getStringValue())
                    .replace("{prop}", prop.getName()).replace("{separator}", separator).replace("{value}", val.toString()));
                }
              }
        }
    }

    @Nullable
    private WorldChunk getChunk(ChunkPos chunkPos)
    {
        CompletableFuture<WorldChunk> future = this.chunkFutures.get(chunkPos);

        if (future == null)
        {
            future = this.setupChunkFuture(chunkPos);
        }

        return future.getNow(null);
    }

    private CompletableFuture<WorldChunk> setupChunkFuture(ChunkPos chunkPos)
    {
        IntegratedServer server = this.mc.getServer();
        CompletableFuture<WorldChunk> future = null;

        if (server != null)
        {
            ServerWorld world = server.getWorld(this.mc.world.getRegistryKey());

            if (world != null)
            {
                future = world.getChunkManager().getChunkFutureSyncOnMainThread(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false)
                        .thenApply((either) -> either.map((chunk) -> (WorldChunk) chunk, (unloaded) -> null) );
            }
        }

        if (future == null)
        {
            future = CompletableFuture.completedFuture(this.getClientChunk(chunkPos));
        }

        this.chunkFutures.put(chunkPos, future);

        return future;
    }

    private WorldChunk getClientChunk(ChunkPos chunkPos)
    {
        if (this.cachedClientChunk == null || this.cachedClientChunk.getPos().equals(chunkPos) == false)
        {
            this.cachedClientChunk = this.mc.world.getChunk(chunkPos.x, chunkPos.z);
        }

        return this.cachedClientChunk;
    }

    private void resetCachedChunks()
    {
        this.chunkFutures.clear();
        this.cachedClientChunk = null;
    }

    private class StringHolder implements Comparable<StringHolder>
    {
        public final String str;

        public StringHolder(String str)
        {
            this.str = str;
        }

        @Override
        public int compareTo(StringHolder other)
        {
            int lenThis = this.str.length();
            int lenOther = other.str.length();

            if (lenThis == lenOther)
            {
                return 0;
            }

            return this.str.length() > other.str.length() ? -1 : 1;
        }
    }

    private static class LinePos implements Comparable<LinePos>
    {
        private final int position;
        private final InfoToggle type;

        private LinePos(int position, InfoToggle type)
        {
            this.position = position;
            this.type = type;
        }

        @Override
        public int compareTo(LinePos other)
        {
            if (this.position < 0)
            {
                return other.position >= 0 ? 1 : 0;
            }
            else if (other.position < 0 && this.position >= 0)
            {
                return -1;
            }

            return this.position < other.position ? -1 : (this.position > other.position ? 1 : 0);
        }
    }
}
