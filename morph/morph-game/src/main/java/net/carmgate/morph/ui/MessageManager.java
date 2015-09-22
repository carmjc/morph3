package net.carmgate.morph.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.carmgate.morph.conf.Conf;

@Singleton
public class MessageManager {

	public static class Message {
		private final String str;
		private long creationTime;

		public Message(String str) {
			this.str = str;
		}

		public long getCreationTime() {
			return creationTime;
		}

		public String getStr() {
			return str;
		}

		protected void setCreationTime(long creationTime) {
			this.creationTime = creationTime;
		}
	}


	@Inject private Conf conf;

	private final List<Message> messages = new ArrayList<>();
	private final List<Message> messagesToBeDeleted = new ArrayList<>();

	private Integer expirationPeriod;

	public void addMessage(Message msg, long creationTime) {
		msg.setCreationTime(creationTime);
		messages.add(msg);
	}

	public void execute(long time) {
		// render message
		for (Message msg : messages) {
			if (msg.getCreationTime() + getExpirationPeriod() < time) {
				messagesToBeDeleted.add(msg);
			}
		}

		// delete messages pending deletion
		for (Message msg : messagesToBeDeleted) {
			messages.remove(msg);
		}
		messagesToBeDeleted.clear();
	}

	public int getExpirationPeriod() {
		if (expirationPeriod == null) {
			expirationPeriod = conf.getIntProperty("messages.expirationPeriod");
		}
		return expirationPeriod;
	}

	public List<Message> getMessages() {
		return messages;
	}
}
