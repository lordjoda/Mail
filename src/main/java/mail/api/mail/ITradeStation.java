/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package mail.api.mail;

import mail.core.inventory.IInventoryAdapter;

public interface ITradeStation extends ILetterHandler,IInventoryAdapter {
	String SAVE_NAME = "TradePO_";
	short SLOT_TRADEGOOD = 0;
	short SLOT_TRADEGOOD_COUNT = 1;
	short SLOT_EXCHANGE_1 = 1;
	short SLOT_EXCHANGE_COUNT = 4;
	short SLOT_LETTERS_1 = 5;
	short SLOT_LETTERS_COUNT = 6;
	short SLOT_STAMPS_1 = 11;
	short SLOT_STAMPS_COUNT = 4;
	short SLOT_RECEIVE_BUFFER = 15;
	short SLOT_RECEIVE_BUFFER_COUNT = 15;
	short SLOT_SEND_BUFFER = 30;
	short SLOT_SEND_BUFFER_COUNT = 10;
	int SLOT_SIZE = SLOT_TRADEGOOD_COUNT + SLOT_EXCHANGE_COUNT + SLOT_LETTERS_COUNT + SLOT_STAMPS_COUNT + SLOT_RECEIVE_BUFFER_COUNT + SLOT_SEND_BUFFER_COUNT;

	IMailAddress getAddress();

	boolean isValid();

	void invalidate();

	void setVirtual(boolean isVirtual);

	boolean isVirtual();

	ITradeStationInfo getTradeInfo();

}
