package io.github.cuubecon.powerfulbeacon.screen;

import io.github.cuubecon.powerfulbeacon.PowerfulBeacon;
import io.github.cuubecon.powerfulbeacon.network.PowerfulBeaconPacketHandler;
import io.github.cuubecon.powerfulbeacon.network.cUpdatePowerfulBeaconPacket;
import io.github.cuubecon.powerfulbeacon.container.PowerfulBeaconContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PowerfulBeaconScreen extends ContainerScreen<PowerfulBeaconContainer> {

    private static final ResourceLocation BEACON_LOCATION = new ResourceLocation(PowerfulBeacon.MODID,"textures/gui/powerful_beacon.png");
    private static final ITextComponent PRIMARY_EFFECT_LABEL = new TranslationTextComponent("block.minecraft.beacon.primary");
    private static final ITextComponent SECONDARY_EFFECT_LABEL = new TranslationTextComponent("block.minecraft.beacon.secondary");
    private static final ITextComponent POWERFUL_EFFECT_LABEL = new TranslationTextComponent("block.powerfulbeacon.powerful_beacon.power");

    private PowerfulBeaconScreen.ConfirmButton confirmButton;
    private boolean initPowerButtons;
    private Effect primary;
    private Effect secondary;
    private int power;
    private int active;

    public PowerfulBeaconScreen(final PowerfulBeaconContainer p_i51102_1_, PlayerInventory p_i51102_2_, ITextComponent p_i51102_3_) {
        super(p_i51102_1_, p_i51102_2_, p_i51102_3_);
        this.imageWidth = 230;
        this.imageHeight = 219;
        p_i51102_1_.addSlotListener(new IContainerListener() {
            public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
            }

            public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
            }

            public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
                PowerfulBeaconScreen.this.primary = p_i51102_1_.getPrimaryEffect();
                PowerfulBeaconScreen.this.secondary = p_i51102_1_.getSecondaryEffect();
                PowerfulBeaconScreen.this.initPowerButtons = true;
                PowerfulBeaconScreen.this.power = p_i51102_1_.isOnCustomBase();
                PowerfulBeaconScreen.this.active = p_i51102_1_.isActive();
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.confirmButton = this.addButton(new PowerfulBeaconScreen.ConfirmButton(this.leftPos + 164, this.topPos + 107));
        this.addButton(new PowerfulBeaconScreen.CancelButton(this.leftPos + 190, this.topPos + 107));
        this.initPowerButtons = true;
        this.confirmButton.active = false;
    }

    @Override
    public void tick() {
        super.tick();
        int i = this.menu.getLevels();
        if (this.initPowerButtons && i >= 0) {
            this.initPowerButtons = false;

            for(int j = 0; j <= 2; ++j) {
                int k = BeaconTileEntity.BEACON_EFFECTS[j].length;
                int l = k * 22 + (k - 1) * 2;

                for(int i1 = 0; i1 < k; ++i1) {
                    Effect effect = BeaconTileEntity.BEACON_EFFECTS[j][i1];
                    PowerfulBeaconScreen.PowerButton beaconscreen$powerbutton = new PowerfulBeaconScreen.PowerButton(this.leftPos + 76 + i1 * 24 - l / 2, this.topPos + 22 + j * 25, effect, true);
                    this.addButton(beaconscreen$powerbutton);
                    if (j >= i) {
                        beaconscreen$powerbutton.active = false;
                    } else if (effect == this.primary) {
                        beaconscreen$powerbutton.setSelected(true);
                    }
                }
            }

            int j1 = 3;
            int k1 = BeaconTileEntity.BEACON_EFFECTS[3].length + 1;
            int l1 = k1 * 22 + (k1 - 1) * 2;

            for(int i2 = 0; i2 < k1 - 1; ++i2) {
                Effect effect1 = BeaconTileEntity.BEACON_EFFECTS[3][i2];
                PowerfulBeaconScreen.PowerButton beaconscreen$powerbutton2 = new PowerfulBeaconScreen.PowerButton(this.leftPos + 187 + i2 * 24, this.topPos + 22, effect1, false);
                this.addButton(beaconscreen$powerbutton2);
                if (3 >= i) {
                    beaconscreen$powerbutton2.active = false;
                } else if (effect1 == this.secondary) {
                    beaconscreen$powerbutton2.setSelected(true);
                }
            }

            if (this.primary != null) {
                PowerfulBeaconScreen.PowerButton beaconscreen$powerbutton1 =
                        new PowerfulBeaconScreen.PowerButton(this.leftPos + 127 + (k1 - 1) * 24 - l1 / 2, this.topPos + 22, this.primary, false);
                this.addButton(beaconscreen$powerbutton1);
                if (3 >= i) {
                    beaconscreen$powerbutton1.active = false;
                } else if (this.primary == this.secondary) {
                    beaconscreen$powerbutton1.setSelected(true);
                }
            }
            PowerfulBeaconScreen.PowerfulButton beaconscreen$powerbutton3 =
                    new PowerfulBeaconScreen.PowerfulButton(this.leftPos + 86 + 3 * 24, this.topPos + 22 + 2 * 25);
            this.addButton(beaconscreen$powerbutton3);
            if(this.power == 0)
                beaconscreen$powerbutton3.active = false;
            else if(this.active == 1)
                beaconscreen$powerbutton3.setSelected(true);
        }

        this.confirmButton.active = this.menu.hasPayment() && ( this.primary != null || this.active == 1);
    }

    @Override
    protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
        drawCenteredString(p_230451_1_, this.font, PRIMARY_EFFECT_LABEL, 62, 10, 14737632);
        drawCenteredString(p_230451_1_, this.font, SECONDARY_EFFECT_LABEL, 169, 10, 14737632);
        drawCenteredString(p_230451_1_, this.font, POWERFUL_EFFECT_LABEL, 169, 50, 14737632);

        for(Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(p_230451_1_, p_230451_2_ - this.leftPos, p_230451_3_ - this.topPos);
                break;
            }
        }

    }

    @Override
    protected void renderBg(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BEACON_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(p_230450_1_, i, j, 0, 0, this.imageWidth, this.imageHeight);
        this.itemRenderer.blitOffset = 100.0F;
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD_BLOCK), i + 41, j + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND_BLOCK), i + 41 + 22, j + 109);
        this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHER_STAR), i + 42 + 44, j + 109);
        this.itemRenderer.blitOffset = 0.0F;
    }
//tese
    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.renderBackground(p_230430_1_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class Button extends AbstractButton {
        private boolean selected;

        protected Button(int p_i50826_1_, int p_i50826_2_) {
            super(p_i50826_1_, p_i50826_2_, 22, 22, StringTextComponent.EMPTY);
        }

        public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
            Minecraft.getInstance().getTextureManager().bind(PowerfulBeaconScreen.BEACON_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = 219;
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.selected) {
                j += this.width * 1;
            } else if (this.isHovered()) {
                j += this.width * 3;
            }

            this.blit(p_230431_1_, this.x, this.y, j, 219, this.width, this.height);
            this.renderIcon(p_230431_1_);
        }

        protected abstract void renderIcon(MatrixStack p_230454_1_);

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean p_146140_1_) {
            this.selected = p_146140_1_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class CancelButton extends PowerfulBeaconScreen.SpriteButton {
        public CancelButton(int p_i50829_2_, int p_i50829_3_) {
            super(p_i50829_2_, p_i50829_3_, 112, 220);
        }

        public void onPress() {
            PowerfulBeaconScreen.this.minecraft.player.connection.send(new CCloseWindowPacket(PowerfulBeaconScreen.this.minecraft.player.containerMenu.containerId));
            PowerfulBeaconScreen.this.minecraft.setScreen((Screen)null);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            PowerfulBeaconScreen.this.renderTooltip(p_230443_1_, DialogTexts.GUI_CANCEL, p_230443_2_, p_230443_3_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ConfirmButton extends PowerfulBeaconScreen.SpriteButton {
        public ConfirmButton(int p_i50828_2_, int p_i50828_3_) {
            super(p_i50828_2_, p_i50828_3_, 90, 220);
        }

        public void onPress() {
            PowerfulBeaconPacketHandler.INSTANCE.sendToServer(new cUpdatePowerfulBeaconPacket(Effect.getId(PowerfulBeaconScreen.this.primary), Effect.getId(PowerfulBeaconScreen.this.secondary), PowerfulBeaconScreen.this.active));
            PowerfulBeaconScreen.this.minecraft.player.connection.send(new CCloseWindowPacket(PowerfulBeaconScreen.this.minecraft.player.containerMenu.containerId));
            PowerfulBeaconScreen.this.minecraft.setScreen((Screen)null);
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            PowerfulBeaconScreen.this.renderTooltip(p_230443_1_, DialogTexts.GUI_DONE, p_230443_2_, p_230443_3_);
        }
    }
    @OnlyIn(Dist.CLIENT)
    class PowerfulButton extends PowerfulBeaconScreen.Button {
        private final TextureAtlasSprite sprite;
        private final ITextComponent tooltip;

        public PowerfulButton(int p_i50827_2_, int p_i50827_3_) {
            super(p_i50827_2_, p_i50827_3_);
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(Effect.byId(5));
            this.tooltip = this.createTooltip();
        }

        private ITextComponent createTooltip() {
            return new TranslationTextComponent("effect.powerfulbeacon.powerful_beacon.power");
        }

        public void onPress() {
            if (!this.isSelected())
                PowerfulBeaconScreen.this.active = 1;
            else
                PowerfulBeaconScreen.this.active = 0;
            PowerfulBeaconScreen.this.buttons.clear();
            PowerfulBeaconScreen.this.children.clear();
            PowerfulBeaconScreen.this.init();
            PowerfulBeaconScreen.this.tick();

        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            PowerfulBeaconScreen.this.renderTooltip(p_230443_1_, this.tooltip, p_230443_2_, p_230443_3_);
        }

        protected void renderIcon(MatrixStack p_230454_1_) {
            Minecraft.getInstance().getTextureManager().bind(this.sprite.atlas().location());
            blit(p_230454_1_, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
        }
    }
    @OnlyIn(Dist.CLIENT)
    class PowerButton extends PowerfulBeaconScreen.Button {
        private final Effect effect;
        private final TextureAtlasSprite sprite;
        private final boolean isPrimary;
        private final ITextComponent tooltip;

        public PowerButton(int p_i50827_2_, int p_i50827_3_, Effect p_i50827_4_, boolean p_i50827_5_) {
            super(p_i50827_2_, p_i50827_3_);
            this.effect = p_i50827_4_;
            this.sprite = Minecraft.getInstance().getMobEffectTextures().get(p_i50827_4_);
            this.isPrimary = p_i50827_5_;
            this.tooltip = this.createTooltip(p_i50827_4_, p_i50827_5_);
        }

        private ITextComponent createTooltip(Effect p_243337_1_, boolean p_243337_2_) {
            IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(p_243337_1_.getDescriptionId());
            if (!p_243337_2_ && p_243337_1_ != Effects.REGENERATION) {
                iformattabletextcomponent.append(" II");
            }

            return iformattabletextcomponent;
        }

        public void onPress() {
            if (!this.isSelected()) {
                if (this.isPrimary) {
                    PowerfulBeaconScreen.this.primary = this.effect;
                } else {
                    PowerfulBeaconScreen.this.secondary = this.effect;
                }

                PowerfulBeaconScreen.this.buttons.clear();
                PowerfulBeaconScreen.this.children.clear();
                PowerfulBeaconScreen.this.init();
                PowerfulBeaconScreen.this.tick();
            }
        }

        public void renderToolTip(MatrixStack p_230443_1_, int p_230443_2_, int p_230443_3_) {
            PowerfulBeaconScreen.this.renderTooltip(p_230443_1_, this.tooltip, p_230443_2_, p_230443_3_);
        }

        protected void renderIcon(MatrixStack p_230454_1_) {
            Minecraft.getInstance().getTextureManager().bind(this.sprite.atlas().location());
            blit(p_230454_1_, this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class SpriteButton extends PowerfulBeaconScreen.Button {
        private final int iconX;
        private final int iconY;

        protected SpriteButton(int p_i50825_1_, int p_i50825_2_, int p_i50825_3_, int p_i50825_4_) {
            super(p_i50825_1_, p_i50825_2_);
            this.iconX = p_i50825_3_;
            this.iconY = p_i50825_4_;
        }

        protected void renderIcon(MatrixStack p_230454_1_) {
            this.blit(p_230454_1_, this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
        }
    }
}
