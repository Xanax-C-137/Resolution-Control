package cc.modlabs.resolutioncontrol.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import cc.modlabs.resolutioncontrol.client.gui.screen.MainSettingsScreen;

public final class ModMenuInfo implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return MainSettingsScreen::new;
	}
}
