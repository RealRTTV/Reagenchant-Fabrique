package ca.rttv.reagenchant;

import ca.rttv.reagenchant.config.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class Reagenchant implements ModInitializer {
	public static final Identifier withoutReagent = new Identifier("reagenchant", "textures/reagent_enchanting_table_without_reagent.png");
	public static final Identifier withReagent = new Identifier("reagenchant", "textures/reagent_enchanting_table_with_reagent.png");
	public static final float configVersion = 1.0f;
	public static Item reagent;

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
