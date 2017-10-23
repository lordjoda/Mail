package mail.mail.compat;

import mail.core.utils.JeiUtil;
import mail.mail.PluginMail;
import mail.mail.blocks.BlockRegistryMail;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class MailJeiPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		BlockRegistryMail blocks = PluginMail.getBlocks();
		JeiUtil.addDescription(registry,
				blocks.mailbox,
				blocks.stampCollector,
				blocks.tradeStation
		);
	}
}
