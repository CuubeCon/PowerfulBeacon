package io.github.cuubecon.powerfulbeacon.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class PowerfulBeaconContainer  extends Container {
    private final IInventory beacon = new Inventory(1) {
        public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
            return p_94041_2_.getItem().is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        public int getMaxStackSize() {
            return 1;
        }
    };
    private final PowerfulBeaconContainer.BeaconSlot paymentSlot;
    private final IWorldPosCallable access;
    private final IIntArray beaconData;

    public PowerfulBeaconContainer(int id, IInventory beacon, IWorldPosCallable access, IIntArray beaconData) {
        super(ModContainers.POWERFUL_BEACON_CONTAINER.get(), id);
        checkContainerDataCount(beaconData, 3);
        this.paymentSlot =  new PowerfulBeaconContainer.BeaconSlot(this.beacon, 0, 136, 110);;

        this.access = access;
        this.beaconData = beaconData;
        this.addSlot(this.paymentSlot);
        this.addDataSlots(beaconData);
        int i = 36;
        int j = 137;

        for(int k = 0; k < 3; ++k) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(beacon, l + k * 9 + 9, 36 + l * 18, 137 + k * 18));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(beacon, i1, 36 + i1 * 18, 195));
        }
    }

    @Override
    public void removed(PlayerEntity playerEntity) {
        super.removed(playerEntity);
        if (!playerEntity.level.isClientSide) {
            ItemStack itemstack = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
            if (!itemstack.isEmpty()) {
                playerEntity.drop(itemstack, false);
            }

        }
    }


    @Override
    public boolean stillValid(PlayerEntity playerIn) {
       return  true; //TODO: PlayerinDistance??
    }


    @Override
    public void setData(int p_75137_1_, int p_75137_2_) {
        super.setData(p_75137_1_, p_75137_2_);
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_82846_2_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_82846_2_ == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (this.moveItemStackTo(itemstack1, 0, 1, false)) { //Forge Fix Shift Clicking in beacons with stacks larger then 1.
                return ItemStack.EMPTY;
            } else if (p_82846_2_ >= 1 && p_82846_2_ < 28) {
                if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (p_82846_2_ >= 28 && p_82846_2_ < 37) {
                if (!this.moveItemStackTo(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_82846_1_, itemstack1);
        }

        return itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getLevels() {
        return this.beaconData.get(0);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Effect getPrimaryEffect() {
        return Effect.byId(this.beaconData.get(1));
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Effect getSecondaryEffect() {
        return Effect.byId(this.beaconData.get(2));
    }

    public void updateEffects(int p_216966_1_, int p_216966_2_) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(1, p_216966_1_);
            this.beaconData.set(2, p_216966_2_);
            this.paymentSlot.remove(1);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasPayment() {
        return !this.beacon.getItem(0).isEmpty();
    }

    class BeaconSlot extends Slot {
        public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_) {
            super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
        }

        public boolean mayPlace(ItemStack p_75214_1_) {
            return p_75214_1_.getItem().is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        public int getMaxStackSize() {
            return 1;
        }
    }
}
