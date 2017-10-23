package mail.mail;

import mail.api.mail.IPostalState;
import mail.core.utils.Translator;

public class ResponseNotMailable implements IPostalState {
	private final IPostalState state;

	public ResponseNotMailable(IPostalState state) {
		this.state = state;
	}

	@Override
	public boolean isOk() {
		return false;
	}

	@Override
	public String getDescription() {
		return Translator.translateToLocalFormatted("for.chat.mail.response.not.mailable.format", state.getDescription());
	}
}
