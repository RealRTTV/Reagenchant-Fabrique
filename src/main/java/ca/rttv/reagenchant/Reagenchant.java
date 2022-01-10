package ca.rttv.reagenchant;

import ca.rttv.reagenchant.config.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Reagenchant implements ModInitializer {
	public static final Identifier withoutReagent = new Identifier("reagenchant", "textures/reagent_enchanting_table_without_reagent.png");
	public static final Identifier withReagent = new Identifier("reagenchant", "textures/reagent_enchanting_table_with_reagent.png");
	public static final float configVersion = 1.2f;

	@Override
	public void onInitialize() {
		blaze_powder.loadConfigs();
		blue_ice.loadConfigs();
		diamond.loadConfigs();
		glowstone_dust.loadConfigs();
		gold_ingot.loadConfigs();
		gunpowder.loadConfigs();
		iron_ingot.loadConfigs();
		nether_star.loadConfigs();
		obsidian.loadConfigs();
		prismarine_crystals.loadConfigs();
		prismarine_shard.loadConfigs();
		redstone.loadConfigs();
	}
}
