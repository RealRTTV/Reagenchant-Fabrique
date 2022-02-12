package ca.rttv.reagenchant.mixin;

import ca.rttv.reagenchant.config.extra.JsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( EnchantmentScreenHandler.class )
public abstract class EnchantmentScreenHandlerMixin extends ScreenHandler {
   
   private final int[]     decrements = new int[3];
   @Final
   @Shadow
   public        Inventory inventory;
   @Shadow
   @Final
   private       Random    random;
   
   protected EnchantmentScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
      super(type, syncId);
   }
   
   @Inject( method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At( value = "INVOKE",
                      target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                      ordinal = 1 ) )
   private void addReagentSlot(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {
      this.addSlot(new Slot(this.inventory, 2, 42, 47) {
         @Override
         public boolean canInsert(ItemStack stack) {
            boolean isValidItem = false;
            for (File file : Objects.requireNonNull(JsonHelper.getConfigDirectory().listFiles())) {
               try {
                  isValidItem = Registry.ITEM.getId(stack.getItem()).toString().equals(JsonParser.parseString(new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"))).getAsJsonObject().get("item").getAsString());
               } catch (FileNotFoundException e) {
                  break;
               }
               if (isValidItem) {
                  break;
               }
            }
            return isValidItem;
         }
      });
   }
   
   @ModifyArg( method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
               at = @At( value = "INVOKE",
                         target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                         ordinal = 0 ),
               index = 0 )
   private Slot changeItemEnchantX(Slot slot) {
      return new Slot(slot.inventory, slot.getIndex(), slot.x - 9, slot.y);
   }
   
   @ModifyArg( method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
               at = @At( value = "INVOKE",
                         target = "Lnet/minecraft/screen/EnchantmentScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                         ordinal = 1 ),
               index = 0 )
   private Slot changeItemLapisX(Slot slot) {
      return new Slot(slot.inventory, slot.getIndex(), slot.x - 11, slot.y);
   }
   
   @ModifyArg( method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
               at = @At( value = "INVOKE",
                         target = "Lnet/minecraft/screen/EnchantmentScreenHandler$1;<init>(Lnet/minecraft/screen/EnchantmentScreenHandler;I)V",
                         ordinal = 0 ) )
   private int changeMaxSlotCount(int size) {
      return 3;
   }
   
   /**
    * this shift took me 3 days to find out,
    * in injecting inbetween of the [STRING LOAD / ALOAD] operation
    * and the [STRING RETURN / ARETURN] operation which means
    * I don't affect the variable because it's happening after
    * the value is grabbed, I just had to shift it an opcode before
    */
   @ModifyVariable( method = "generateEnchantments",
                    at = @At( value = "RETURN",
                              ordinal = 0,
                              shift = At.Shift.BEFORE ),
                    index = 4 )
   private List<EnchantmentLevelEntry> modifyEnchantments(List<EnchantmentLevelEntry> list, ItemStack stack, int slot, int level) throws FileNotFoundException {
      if (inventory.getStack(2).getItem() != Items.AIR) {
         List<EnchantmentLevelEntry> reagentEntries = getReagentEntries(this.random, level, slot);
         List<EnchantmentLevelEntry> concat = new ArrayList<>(reagentEntries);
         concat.addAll(list);
         for (int i = 0; i < concat.size(); i++) {
            for (int j = i; j < concat.size(); j++) {
               if (i != j && !concat.get(i).enchantment.canCombine(concat.get(j).enchantment)) {
                  concat.remove(j--); // thanks intellij
               }
            }
         }
         return concat;
      }
      return list;
   }
   
   /**
    * this is the getReagentEntries method
    * this method in simple does what the
    * vanilla {@link EnchantmentHelper#getPossibleEntries(int, ItemStack, boolean)}
    * method does but for only the entries
    * in the current reagent's {@code enchantments}
    * array, this array can be found at
    * the respective config file.
    *
    * @param random a mirrored version of the enchantment random
    * @param power  bookshelf power for enchant level
    * @param slot   the slot the enchantment is being generated in
    *
    * @return all the enchantments the item can get from the current reagent (if rng rolls yes)
    *
    * @throws FileNotFoundException FileReader lol
    */
   private List<EnchantmentLevelEntry> getReagentEntries(Random random, int power, int slot) throws FileNotFoundException {
      int reagentCost = 0;
      File[] files = Objects.requireNonNull(JsonHelper.getConfigDirectory().listFiles());
      JsonObject reagentObject = null;
      List<EnchantmentLevelEntry> newList = new ArrayList<>();
      for (File file : files) {
         reagentObject = JsonParser.parseString(new BufferedReader(new FileReader(file)).lines().collect(Collectors.joining("\n"))).getAsJsonObject();
         String jsonItem = reagentObject.get("item").getAsString();
         if (jsonItem.equals(Registry.ITEM.getId(inventory.getStack(2).getItem()).toString())) {
            break;
         }
      }
      
      for (int i = 0; i < Objects.requireNonNull(reagentObject).getAsJsonArray("enchantments").size(); i++) {
         if (random.nextDouble() <= reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("probability").getAsFloat()) {
            Enchantment enchantment = Objects.requireNonNull(Registry.ENCHANTMENT.get(new Identifier(reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("enchantment").getAsString())));
            if (enchantment.type.isAcceptableItem(this.inventory.getStack(0).getItem())) {
               
               reagentCost += reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("reagentCost").getAsInt();
               
               int minLevel = reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("minimumEnchantmentLevel").getAsInt();
               int maxLevel = reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("maximumEnchantmentLevel").getAsInt();
               power += reagentObject.getAsJsonArray("enchantments").get(i).getAsJsonObject().get("bonusPower").getAsInt();
               if (enchantment.getMinPower(minLevel) > power) {
                  newList.add(new EnchantmentLevelEntry(enchantment, enchantment.getMinLevel()));
               }
               
               for (int level = maxLevel; level >= enchantment.getMinLevel(); --level) {
                  if (power >= enchantment.getMinPower(level)) {
                     if (power <= enchantment.getMaxPower(level)) {
                        newList.add(new EnchantmentLevelEntry(enchantment, level));
                        break;
                     }
                  }
               }
            }
         }
      }
      if (this.inventory.getStack(2).getCount() >= reagentCost) {
         this.decrements[slot] = reagentCost;
         return newList;
      }
      return new ArrayList<>();
   }
   
   @Inject( method = "method_17410",
            at = @At( value = "INVOKE",
                      target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V" ) )
   private void onButtonClick(ItemStack itemStack, int i, PlayerEntity playerEntity, int j, ItemStack itemStack2, World world, BlockPos pos, CallbackInfo ci) {
      ItemStack stack = this.inventory.getStack(2);
      
      if (!playerEntity.getAbilities().creativeMode && stack.getCount() >= decrements[i]) {
         stack.decrement(decrements[i]);
      }
   }
   
   @Inject( method = "onContentChanged",
            at = @At( value = "INVOKE",
                      target = "Lnet/minecraft/inventory/Inventory;getStack(I)Lnet/minecraft/item/ItemStack;" ) )
   private void onContentChanged(Inventory inventory, CallbackInfo ci) {
      // I think something's supposed to be here but idk.
   }
   
   @Inject( method = "method_17411",
            at = @At( value = "INVOKE",
                      target = "Lnet/minecraft/util/registry/Registry;getRawId(Ljava/lang/Object;)I" ) )
   private void setItemsSlot(ItemStack itemStack, World world, BlockPos pos, CallbackInfo ci) {
      // I think something's supposed to be here but idk.
   }
}
