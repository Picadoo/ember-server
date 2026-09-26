package net.minecraft.server;

import java.util.Arrays;
import java.util.Iterator;

// CraftBukkit start
import java.util.List;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.inventory.InventoryHolder;
// CraftBukkit end

public class TileEntityBrewingStand extends TileEntityContainer implements ITickable, IWorldInventory {

    private static final int[] a = new int[] { 3};
    private static final int[] f = new int[] { 0, 1, 2, 3};
    private static final int[] g = new int[] { 0, 1, 2, 4};
    private NonNullList<ItemStack> items;
    private int brewTime;
    private boolean[] j;
    private Item k;
    private String l;
    private int fuelLevel;
    // CraftBukkit start - add fields and methods
    private int lastTick = MinecraftServer.currentTick;
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = 64;

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public List<ItemStack> getContents() {
        return this.items;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityBrewingStand() {
        this.items = NonNullList.a(5, ItemStack.a);
    }

    public String getName() {
        return this.hasCustomName() ? this.l : "container.brewing";
    }

    public boolean hasCustomName() {
        return this.l != null && !this.l.isEmpty();
    }

    public void setCustomName(String s) {
        this.l = s;
    }

    public int getSize() {
        return this.items.size();
    }

    public boolean x_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public void e() {
        ItemStack itemstack = (ItemStack) this.items.get(4);

        if (this.fuelLevel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
            // CraftBukkit start
            BrewingStandFuelEvent event = new BrewingStandFuelEvent(world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), CraftItemStack.asCraftMirror(itemstack), 20);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            this.fuelLevel = event.getFuelPower(); // PAIL fuelLevel
            if (this.fuelLevel > 0 && event.isConsuming()) {
                itemstack.subtract(1);
            }
            // CraftBukkit end
            this.update();
        }

        boolean flag = this.o();
        boolean flag1 = this.brewTime > 0;
        ItemStack itemstack1 = (ItemStack) this.items.get(3);

        // CraftBukkit start - Use wall time instead of ticks for brewing
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.lastTick = MinecraftServer.currentTick;

        if (flag1) {
            this.brewTime -= elapsedTicks;
            boolean flag2 = this.brewTime <= 0; // == -> <=
            // CraftBukkit end

            if (flag2 && flag) {
                this.p();
                this.update();
            } else if (!flag) {
                this.brewTime = 0;
                this.update();
            } else if (this.k != itemstack1.getItem()) {
                this.brewTime = 0;
                this.update();
            }
        } else if (flag && this.fuelLevel > 0) {
            --this.fuelLevel;
            // CoreSystem hybrid — custom brew duration (default 400)
            org.bukkit.inventory.ItemStack bukkitIngredient = CraftItemStack.asCraftMirror(itemstack1);
            org.bukkit.inventory.ItemStack[] bukkitBottles = new org.bukkit.inventory.ItemStack[3];
            for (int bi = 0; bi < 3; ++bi) {
                bukkitBottles[bi] = CraftItemStack.asCraftMirror((ItemStack) this.items.get(bi));
            }
            this.brewTime = pers.coresystem.paper.BrewNmsHooks.resolveBrewTime(bukkitIngredient, bukkitBottles);
            this.k = itemstack1.getItem();
            this.update();
        }

        if (!this.world.isClientSide) {
            boolean[] aboolean = this.n();

            if (!Arrays.equals(aboolean, this.j)) {
                this.j = aboolean;
                IBlockData iblockdata = this.world.getType(this.getPosition());

                if (!(iblockdata.getBlock() instanceof BlockBrewingStand)) {
                    return;
                }

                for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
                    iblockdata = iblockdata.set(BlockBrewingStand.HAS_BOTTLE[i], Boolean.valueOf(aboolean[i]));
                }

                this.world.setTypeAndData(this.position, iblockdata, 2);
            }
        }

    }

    public boolean[] n() {
        boolean[] aboolean = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    private boolean o() {
        ItemStack itemstack = (ItemStack) this.items.get(3);

        if (itemstack.isEmpty()) {
            return false;
        } else {
            org.bukkit.inventory.ItemStack bukkitIngredient = CraftItemStack.asCraftMirror(itemstack);
            boolean customIngredient = pers.coresystem.paper.BrewNmsHooks.isAllowedIngredient(bukkitIngredient);
            boolean vanillaIngredient = PotionBrewer.a(itemstack);
            if (!customIngredient && !vanillaIngredient) {
                return false;
            }

            org.bukkit.inventory.ItemStack[] bukkitBottles = new org.bukkit.inventory.ItemStack[3];
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = (ItemStack) this.items.get(i);
                bukkitBottles[i] = CraftItemStack.asCraftMirror(itemstack1);
                if (itemstack1.isEmpty()) {
                    continue;
                }
                // Prefer custom match
                if (pers.coresystem.paper.BrewNmsHooks.resolveMatch(bukkitIngredient, bukkitBottles[i]) != null) {
                    return true;
                }
                if (pers.coresystem.paper.BrewNmsHooks.isVanillaEnabled() && PotionBrewer.a(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void p() {
        ItemStack itemstack = (ItemStack) this.items.get(3);
        // CraftBukkit start
        InventoryHolder owner = this.getOwner();
        if (owner != null) {
            BrewEvent event = new BrewEvent(world.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()), (org.bukkit.inventory.BrewerInventory) owner.getInventory(), this.fuelLevel);
            org.bukkit.Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end

        org.bukkit.inventory.ItemStack bukkitIngredient = CraftItemStack.asCraftMirror(itemstack);
        for (int i = 0; i < 3; ++i) {
            ItemStack bottle = (ItemStack) this.items.get(i);
            if (bottle.isEmpty()) {
                continue;
            }
            org.bukkit.inventory.ItemStack bukkitBottle = CraftItemStack.asCraftMirror(bottle);
            pers.coresystem.paper.BrewNmsHooks.BrewMatch match = pers.coresystem.paper.BrewNmsHooks.resolveMatch(bukkitIngredient, bukkitBottle);
            if (match != null && match.result != null) {
                ItemStack custom = CraftItemStack.asNMSCopy(match.result);
                if (!custom.isEmpty()) {
                    int amt = match.resultAmount > 0 ? match.resultAmount : 1;
                    custom.setCount(Math.min(amt, custom.getMaxStackSize()));
                    // Consume one bottle from a raised stack; drop leftovers so multi-stacks are not wiped
                    if (bottle.getCount() > 1) {
                        ItemStack remain = bottle.cloneItemStack();
                        remain.setCount(bottle.getCount() - 1);
                        BlockPosition dropPos = this.getPosition();
                        InventoryUtils.dropItem(this.world, (double) dropPos.getX(), (double) dropPos.getY(), (double) dropPos.getZ(), remain);
                    }
                    this.items.set(i, custom);
                    continue;
                }
            }
            if (pers.coresystem.paper.BrewNmsHooks.isVanillaEnabled()) {
                this.items.set(i, PotionBrewer.d(itemstack, bottle));
            }
        }

        itemstack.subtract(1);
        BlockPosition blockposition = this.getPosition();

        if (itemstack.getItem().r()) {
            ItemStack itemstack1 = new ItemStack(itemstack.getItem().q());

            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else {
                InventoryUtils.dropItem(this.world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack1);
            }
        }

        this.items.set(3, itemstack);
        this.world.triggerEffect(1035, blockposition, 0);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, (DataInspector) (new DataInspectorItemList(TileEntityBrewingStand.class, new String[] { "Items"})));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        ContainerUtil.b(nbttagcompound, this.items);
        this.brewTime = nbttagcompound.getShort("BrewTime");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.l = nbttagcompound.getString("CustomName");
        }

        this.fuelLevel = nbttagcompound.getByte("Fuel");
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setShort("BrewTime", (short) this.brewTime);
        ContainerUtil.a(nbttagcompound, this.items);
        if (this.hasCustomName()) {
            nbttagcompound.setString("CustomName", this.l);
        }

        nbttagcompound.setByte("Fuel", (byte) this.fuelLevel);
        return nbttagcompound;
    }

    public ItemStack getItem(int i) {
        return i >= 0 && i < this.items.size() ? (ItemStack) this.items.get(i) : ItemStack.a;
    }

    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.items, i, j);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, i);
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.items.size()) {
            this.items.set(i, itemstack);
        }

    }

    public int getMaxStackSize() {
        // CoreSystem — raise TE inventory ceiling when brew stack limits enabled
        int hooked = pers.coresystem.paper.BrewNmsHooks.getSlotMaxStackSize();
        return hooked > 0 ? hooked : this.maxStack; // CraftBukkit
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        if (i == 3) {
            // CoreSystem hybrid — allow custom ingredients
            return PotionBrewer.a(itemstack) || pers.coresystem.paper.BrewNmsHooks.isAllowedIngredient(CraftItemStack.asCraftMirror(itemstack));
        } else {
            Item item = itemstack.getItem();

            if (i == 4) {
                return item == Items.BLAZE_POWDER;
            }
            boolean vanillaBottle = item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE;
            org.bukkit.inventory.ItemStack bukkitIn = CraftItemStack.asCraftMirror(itemstack);
            boolean customBottle = pers.coresystem.paper.BrewNmsHooks.isAllowedBottle(bukkitIn)
                    || pers.coresystem.paper.BrewNmsHooks.isAllowedResult(bukkitIn);
            if (!(vanillaBottle || customBottle)) {
                return false;
            }
            ItemStack current = this.getItem(i);
            if (current.isEmpty()) {
                return true;
            }
            // Allow hopper/stack merge into occupied bottle slot for raised-stack custom items only
            int max = pers.coresystem.paper.BrewNmsHooks.getMaxStackSize(bukkitIn);
            if (max <= 0) {
                return false; // vanilla: bottle slots stay empty-only
            }
            return current.getCount() < max
                    && current.getItem() == itemstack.getItem()
                    && ItemStack.equals(current, itemstack);
        }
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? TileEntityBrewingStand.a : (enumdirection == EnumDirection.DOWN ? TileEntityBrewingStand.f : TileEntityBrewingStand.g);
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return this.b(i, itemstack);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return i == 3 ? itemstack.getItem() == Items.GLASS_BOTTLE : true;
    }

    public String getContainerName() {
        return "minecraft:brewing_stand";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerBrewingStand(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return this.brewTime;

        case 1:
            return this.fuelLevel;

        default:
            return 0;
        }
    }

    public void setProperty(int i, int j) {
        switch (i) {
        case 0:
            this.brewTime = j;
            break;

        case 1:
            this.fuelLevel = j;
        }

    }

    public int h() {
        return 2;
    }

    public void clear() {
        this.items.clear();
    }
}
