package io.github.cuubecon.powerfulbeacon.tileentity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.cuubecon.powerfulbeacon.container.PowerfulBeaconContainer;
import io.github.cuubecon.powerfulbeacon.util.ModTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PowerfulBeaconTile  extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final Effect[][] BEACON_EFFECTS = new Effect[][]{{Effects.MOVEMENT_SPEED, Effects.DIG_SPEED}, {Effects.DAMAGE_RESISTANCE, Effects.JUMP}, {Effects.DAMAGE_BOOST}, {Effects.REGENERATION}};
    private static final Set<Effect> VALID_EFFECTS = Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
    private List<PowerfulBeaconTile.BeamSegment> beamSections = Lists.newArrayList();
    private List<PowerfulBeaconTile.BeamSegment> checkingBeamSections = Lists.newArrayList();
    private int levels;
    private int lastCheckY = -1;
    @Nullable
    private Effect primaryPower;
    @Nullable
    private Effect secondaryPower;
    @Nullable
    private ITextComponent name;
    private LockCode lockKey = LockCode.NO_LOCK;
    public final IIntArray dataAccess = new IIntArray() {
        public int get(int p_221476_1_) {
            switch(p_221476_1_) {
                case 0:
                    return PowerfulBeaconTile.this.levels;
                case 1:
                    return Effect.getId(PowerfulBeaconTile.this.primaryPower);
                case 2:
                    return Effect.getId(PowerfulBeaconTile.this.secondaryPower);
                default:
                    return 0;
            }
        }
        //BeaconTileEntityRenderer
        public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
                case 0:
                    PowerfulBeaconTile.this.levels = p_221477_2_;
                    break;
                case 1:
                    if (!PowerfulBeaconTile.this.level.isClientSide && !PowerfulBeaconTile.this.beamSections.isEmpty()) {
                        PowerfulBeaconTile.this.playSound(SoundEvents.BEACON_POWER_SELECT);
                    }

                    PowerfulBeaconTile.this.primaryPower = PowerfulBeaconTile.getValidEffectById(p_221477_2_);
                    break;
                case 2:
                    PowerfulBeaconTile.this.secondaryPower = PowerfulBeaconTile.getValidEffectById(p_221477_2_);
            }

        }

        public int getCount() {
            return 3;
        }
    };
    private int glowlevels = 0;
    private int gildedlevels = 0;
    private int nightvisionlevels = 0;
    private int safevilligarlevels = 0;
    private int haybewlevels = 0;
    private int honeyBlockLevels = 0;

    public PowerfulBeaconTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    public PowerfulBeaconTile() {
        this(ModTileEntitys.POWERFUL_BEACON_TILE.get());
    }




    @Override
    public void tick() {
        int i = this.worldPosition.getX();
        int j = this.worldPosition.getY();
        int k = this.worldPosition.getZ();
        BlockPos blockpos;
        if (this.lastCheckY < j) {
            blockpos = this.worldPosition;
            this.checkingBeamSections = Lists.newArrayList();
            this.lastCheckY = blockpos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, this.lastCheckY + 1, k);
        }

        PowerfulBeaconTile.BeamSegment beacontileentity$beamsegment = this.checkingBeamSections.isEmpty() ? null : this.checkingBeamSections.get(this.checkingBeamSections.size() - 1);
        int l = this.level.getHeight(Heightmap.Type.WORLD_SURFACE, i, k);

        for(int i1 = 0; i1 < 10 && blockpos.getY() <= l; ++i1) {

            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            float[] afloat = blockstate.getBeaconColorMultiplier(this.level, blockpos, getBlockPos());
            if (afloat != null) {
                if (this.checkingBeamSections.size() <= 1) {
                    beacontileentity$beamsegment = new PowerfulBeaconTile.BeamSegment(afloat);
                    this.checkingBeamSections.add(beacontileentity$beamsegment);
                } else if (beacontileentity$beamsegment != null) {
                    if (Arrays.equals(afloat, beacontileentity$beamsegment.color)) {
                        beacontileentity$beamsegment.increaseHeight();
                    } else {
                        beacontileentity$beamsegment = new PowerfulBeaconTile.BeamSegment(new float[]{(beacontileentity$beamsegment.color[0] + afloat[0]) / 2.0F, (beacontileentity$beamsegment.color[1] + afloat[1]) / 2.0F, (beacontileentity$beamsegment.color[2] + afloat[2]) / 2.0F});
                        this.checkingBeamSections.add(beacontileentity$beamsegment);
                    }
                }
            } else {
                if (beacontileentity$beamsegment == null ||  block != Blocks.AIR  || blockstate.getLightBlock(this.level, blockpos) >= 15 && block != Blocks.BEDROCK) {
                    //this.checkingBeamSections.clear();
                    //System.out.println("BEAM SIZE" + this.checkingBeamSections.size());
                    this.lastCheckY = l;
                    break;
                }
                beacontileentity$beamsegment.increaseHeight();
            }

            blockpos = blockpos.above();
            ++this.lastCheckY;
        }

        int j1 = this.levels;

        if (this.level.getGameTime() % 80L == 0L) {
            if (!this.beamSections.isEmpty()) {
                this.updateBase(i, j, k);
            }

            if (this.levels > 0 && !this.beamSections.isEmpty()) {
                this.applyEffects();
                this.playSound(SoundEvents.BEACON_AMBIENT);
            }
            if(this.glowlevels > 0 || this.gildedlevels > 0 || this.nightvisionlevels > 0 || this.safevilligarlevels > 0 || this.haybewlevels > 0 || this.honeyBlockLevels > 0)
            {
                this.applyCustomEffects();
            }
        }

        if (this.lastCheckY >= l) {
            this.lastCheckY = -1;
            boolean flag = j1 > 0;
            this.beamSections = this.checkingBeamSections;
            if (!this.level.isClientSide) {
                boolean flag1 = this.levels > 0;
                if (!flag && flag1) {
                    this.playSound(SoundEvents.BEACON_ACTIVATE);

                    for(ServerPlayerEntity serverplayerentity : this.level.getEntitiesOfClass(ServerPlayerEntity.class, (new AxisAlignedBB((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k)).inflate(10.0D, 5.0D, 10.0D))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayerentity, new BeaconTileEntity());
                    }
                } else if (flag && !flag1) {
                    this.playSound(SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }

    }

    private void updateBase(int p_213927_1_, int p_213927_2_, int p_213927_3_) {
        this.levels = 0;

        for(int i = 1; i <= 4; this.levels = i++) {
            int j = p_213927_2_ - i;
            if (j < 0) {
                break;
            }

            boolean flag = true;

            for(int k = p_213927_1_ - i; k <= p_213927_1_ + i && flag; ++k) {
                for(int l = p_213927_3_ - i; l <= p_213927_3_ + i; ++l) {
                    if (!this.level.getBlockState(new BlockPos(k, j, l)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                        flag = false;
                        break;
                    }
                }
            }

            if (!flag) {
                break;
            }
        }
        checkCustomBase(p_213927_1_, p_213927_2_, p_213927_3_);
    }

    private void checkCustomBase(int p_213927_1_, int p_213927_2_, int p_213927_3_) {


        BlockState blockUnderBeacon = this.level.getBlockState(new BlockPos(p_213927_1_, p_213927_2_-1, p_213927_3_));
        if(blockUnderBeacon.is(ModTags.Blocks.POWERFUL_BEACON_BASE_BLOCKS))
        {
            System.out.println(blockUnderBeacon);
            Block blockToCheck;
            int levelsToAdd = 0;
           if(blockUnderBeacon.is(Blocks.GLOWSTONE))
           {
                blockToCheck = Blocks.GLOWSTONE;
           }
           else if(blockUnderBeacon.is(Blocks.GILDED_BLACKSTONE))
           {
                blockToCheck = Blocks.GILDED_BLACKSTONE;
           }
           else if(blockUnderBeacon.is(Blocks.SEA_LANTERN))
           {
               blockToCheck = Blocks.SEA_LANTERN;
           }
           else if(blockUnderBeacon.is(Blocks.CRYING_OBSIDIAN))
           {
               blockToCheck = Blocks.CRYING_OBSIDIAN;
           }
           else if(blockUnderBeacon.is(Blocks.HAY_BLOCK))
           {
               blockToCheck = Blocks.HAY_BLOCK;
           }
           else if(blockUnderBeacon.is(Blocks.HONEY_BLOCK))
           {
               blockToCheck = Blocks.HONEY_BLOCK;
           }
           else
           {
               return;
           }
            for(int i = 1; i <= 4;  levelsToAdd = i++)
            {
                int j = p_213927_2_ - i;
                if (j < 0) {
                    break;
                }

                boolean flag = true;

                for(int k = p_213927_1_ - i; k <= p_213927_1_ + i && flag; ++k) {
                    for(int l = p_213927_3_ - i; l <= p_213927_3_ + i; ++l) {
                        if (!this.level.getBlockState(new BlockPos(k, j, l)).is(blockToCheck)) {
                            flag = false;
                            break;
                        }
                    }
                }

                if (!flag) {
                    break;
                }
            }

            if(blockUnderBeacon.is(Blocks.GLOWSTONE))
            {
                this.glowlevels = levelsToAdd;
            }
            else if(blockUnderBeacon.is(Blocks.GILDED_BLACKSTONE))
            {
                this.gildedlevels = levelsToAdd;
            }
            else if(blockUnderBeacon.is(Blocks.SEA_LANTERN))
            {
                this.nightvisionlevels = levelsToAdd;
            }
            else if(blockUnderBeacon.is(Blocks.CRYING_OBSIDIAN))
            {
                this.safevilligarlevels = levelsToAdd;
            }
            else if(blockUnderBeacon.is(Blocks.HAY_BLOCK))
            {
                this.haybewlevels = levelsToAdd;
            }
            else if(blockUnderBeacon.is(Blocks.HONEY_BLOCK))
            {
                this.honeyBlockLevels = levelsToAdd;
            }
        }

    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){

        return this.INFINITE_EXTENT_AABB;

    }
    @Override
    public void setRemoved() {
        this.playSound(SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    private void applyCustomEffects()
    {
        System.out.println("GILD LEVELS " + gildedlevels);
        if(this.glowlevels > 0 && !this.level.isClientSide)
        {
           // System.out.println(glowlevels);
            double d0 = this.levels * 10 + 10;
            int j = (9 + this.glowlevels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);

            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity entity : entities) {
                if(!(entity instanceof PlayerEntity))
                    entity.addEffect(new EffectInstance(Effects.GLOWING,j, 0, true,true));
            }

        }
        else if (this.gildedlevels > 0 && !this.level.isClientSide)
        {

            double d0 = this.levels * 10 + 10;
            int j = (9 + this.gildedlevels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);

            List<LivingEntity> entities = this.level.getEntitiesOfClass(ZombifiedPiglinEntity.class, axisalignedbb);
            for (LivingEntity entity : entities) {

                entity.setHealth(0F);
            }
        }
        else if (this.nightvisionlevels > 0 && !this.level.isClientSide)
        {

            double d0 = this.levels * 10 + 10;
            int j = (9 + this.nightvisionlevels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);

            List<PlayerEntity> entities = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);
            for (PlayerEntity entity : entities) {

                entity.addEffect(new EffectInstance(Effects.NIGHT_VISION,j, 0, true,true));

            }
        }
        else if (this.safevilligarlevels > 0 && !this.level.isClientSide)
        {

            double d0 = this.levels * 10 + 10;
            int j = (9 + this.safevilligarlevels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);

            List<VillagerEntity> entities = this.level.getEntitiesOfClass(VillagerEntity.class, axisalignedbb);
            for (VillagerEntity entity : entities)
            {
                entity.invulnerableTime = j;
            }
        }
        else if (this.haybewlevels > 0 && !this.level.isClientSide)
        {

            double d0 = this.levels * 10 + 10;
            int j = (9 + this.haybewlevels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);

            List<PlayerEntity> entities = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);
            for (PlayerEntity entity : entities)
            {
                entity.addEffect(new EffectInstance(Effects.SATURATION,j, 0, true,true));
            }
        }
        else if (this.honeyBlockLevels > 0 && !this.level.isClientSide)
        {

            double d0 = this.levels * 10 + 10;
            int j = (9 + this.honeyBlockLevels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);

            List<PlayerEntity> entities = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);
            for (PlayerEntity entity : entities)
            {
                entity.addEffect(new EffectInstance(Effects.SLOW_FALLING,j, 0, true,true));
            }
        }
    }

    private void applyEffects() {


        if (!this.level.isClientSide && this.primaryPower != null) {
            double d0 = (double)(this.levels * 10 + 10);
            int i = 0;
            if (this.levels >= 4 && this.primaryPower == this.secondaryPower) {
                i = 1;
            }

            int j = (9 + this.levels * 2) * 20;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.worldPosition)).inflate(d0).expandTowards(0.0D, (double)this.level.getMaxBuildHeight(), 0.0D);
            List<PlayerEntity> list = this.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);

            for(PlayerEntity playerentity : list) {
                playerentity.addEffect(new EffectInstance(this.primaryPower, j, i, true, true));
            }

            if (this.levels >= 4 && this.primaryPower != this.secondaryPower && this.secondaryPower != null) {
                for(PlayerEntity playerentity1 : list) {
                    playerentity1.addEffect(new EffectInstance(this.secondaryPower, j, 0, true, true));
                }
            }

        }
    }

    public void playSound(SoundEvent p_205736_1_) {
        this.level.playSound((PlayerEntity)null, this.worldPosition, p_205736_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public List<PowerfulBeaconTile.BeamSegment> getBeamSections() {
        return (List<PowerfulBeaconTile.BeamSegment>)((this.levels == 0 && this.gildedlevels == 0 && this.glowlevels == 0 && this.nightvisionlevels == 0 && this.safevilligarlevels == 0 && this.haybewlevels == 0 && this.honeyBlockLevels == 0)? ImmutableList.of() : this.beamSections);
    }

    public int getLevels() {
        return this.levels;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getViewDistance() {
        return 256.0D;
    }

    @Nullable
    private static Effect getValidEffectById(int p_184279_0_) {
        Effect effect = Effect.byId(p_184279_0_);
        return VALID_EFFECTS.contains(effect) ? effect : null;
    }

    @Override
    public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
        super.load(p_230337_1_, p_230337_2_);
        this.glowlevels = p_230337_2_.getInt("GlowLevels");
        this.gildedlevels = p_230337_2_.getInt("GildedLevels");
        this.nightvisionlevels = p_230337_2_.getInt("NightvisionLevels");
        this.safevilligarlevels = p_230337_2_.getInt("SafeVilligarLevels");
        this.haybewlevels = p_230337_2_.getInt("HaybewLevels");
        this.honeyBlockLevels = p_230337_2_.getInt("HoneyBlockLevels");

        this.primaryPower = getValidEffectById(p_230337_2_.getInt("Primary"));
        this.secondaryPower = getValidEffectById(p_230337_2_.getInt("Secondary"));
        if (p_230337_2_.contains("CustomName", 8)) {
            this.name = ITextComponent.Serializer.fromJson(p_230337_2_.getString("CustomName"));
        }

        this.lockKey = LockCode.fromTag(p_230337_2_);
    }

    @Override
    public CompoundNBT save(CompoundNBT p_189515_1_) {
        super.save(p_189515_1_);
        p_189515_1_.putInt("Primary", Effect.getId(this.primaryPower));
        p_189515_1_.putInt("Secondary", Effect.getId(this.secondaryPower));
        p_189515_1_.putInt("Levels", this.levels);
        p_189515_1_.putInt("GlowLevels", this.glowlevels);
        p_189515_1_.putInt("GildedLevels", this.gildedlevels);
        p_189515_1_.putInt("NightvisionLevels", this.nightvisionlevels);
        p_189515_1_.putInt("SafeVilligarLevels", this.safevilligarlevels);
        p_189515_1_.putInt("HaybewLevels", this.haybewlevels);
        p_189515_1_.putInt("HoneyBlockLevels", this.honeyBlockLevels);
        if (this.name != null) {
            p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
        }

        this.lockKey.addToTag(p_189515_1_);
        return p_189515_1_;
    }

    public void setCustomName(@Nullable ITextComponent p_200227_1_) {
        this.name = p_200227_1_;
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return LockableTileEntity.canUnlock(p_createMenu_3_, this.lockKey, this.getDisplayName()) ? new PowerfulBeaconContainer(p_createMenu_1_, p_createMenu_2_, IWorldPosCallable.create(this.level, this.getBlockPos()),  this.dataAccess) : null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent)(this.name != null ? this.name : new TranslationTextComponent("container.cubetest.powerful_beacon"));
    }

    public static class BeamSegment {
        private final float[] color;
        private int height;

        public BeamSegment(float[] p_i45669_1_) {
            this.color = p_i45669_1_;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        @OnlyIn(Dist.CLIENT)
        public float[] getColor() {
            return this.color;
        }

        @OnlyIn(Dist.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}
