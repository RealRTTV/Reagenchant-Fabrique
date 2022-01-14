package ca.rttv.reagenchant;

import ca.rttv.reagenchant.config.blaze_powder;
import ca.rttv.reagenchant.config.blue_ice;
import ca.rttv.reagenchant.config.diamond;
import ca.rttv.reagenchant.config.glowstone_dust;
import ca.rttv.reagenchant.config.gold_ingot;
import ca.rttv.reagenchant.config.gunpowder;
import ca.rttv.reagenchant.config.iron_ingot;
import ca.rttv.reagenchant.config.nether_star;
import ca.rttv.reagenchant.config.obsidian;
import ca.rttv.reagenchant.config.prismarine_crystals;
import ca.rttv.reagenchant.config.prismarine_shard;
import ca.rttv.reagenchant.config.redstone;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

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
